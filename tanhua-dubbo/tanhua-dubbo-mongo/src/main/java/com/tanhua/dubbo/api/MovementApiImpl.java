package com.tanhua.dubbo.api;


import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void publish(Movement movement) {
        //1、保存动态详情
        try {
            //设置PID
            movement.setPid(idWorker.getNextId("movement"));
            //设置时间
            movement.setCreated(System.currentTimeMillis());
            //保存数据
            mongoTemplate.save(movement);
            //2、查询当前用户的好友数据
            Criteria criteria = Criteria.where("userId").is(movement.getUserId());
            Query query = Query.query(criteria);
            List<Friend> friends = mongoTemplate.find(query, Friend.class);
            //3、循环好友数据，构建时间线数据存入数据库
            for (Friend friend : friends) {
                MovementTimeLine timeLine = new MovementTimeLine();
                timeLine.setMovementId(movement.getId());
                timeLine.setUserId(friend.getUserId());
                timeLine.setFriendId(friend.getFriendId());
                timeLine.setCreated(System.currentTimeMillis());
                mongoTemplate.save(timeLine);
            }
        } catch (Exception e) {
            //忽略事务处理
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //创建Criteria
        Criteria criteria = Criteria.where("userId").is(userId);
        //创建Query对象
        Query query = Query.query(criteria);
        //查询总记录数
        long count = mongoTemplate.count(query, Movement.class);
        //设置分页查询参数
        query.skip((page -1 ) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //查询分页数据列表
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        //构造返回值
        return new PageResult(page,pagesize,count,movements);
    }


}
