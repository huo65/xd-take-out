package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huo.common.GeneralResult;
import com.huo.entity.User;
import com.huo.service.UserService;
import com.huo.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public GeneralResult<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
//        log.info("here&&&&&&&&&&&&&&&&&&&&&&&&");
        String mail = user.getMail();
        if (!mail.isEmpty()) {
            //随机生成一个验证码
            String code = MailUtils.achieveCode();
            log.info(code);
            //这里的phone其实就是邮箱，code是我们生成的验证码
//            MailUtils.sendTestMail(mail, code);TODO 暂时关闭方便测试
            //验证码存session，方便后面拿出来比对
//            session.setAttribute("mail", code);

//            将验证码存入redis
            redisTemplate.opsForValue().set(mail,code);
            return GeneralResult.success("验证码发送成功");
        }
        return GeneralResult.error("验证码发送失败");
    }


    @PostMapping("/login")
    public GeneralResult<User> login(@RequestBody Map map, HttpSession session){
       String mail = map.get("mail").toString();
       String code = map.get("code").toString();

//       String rightCode = session.getAttribute("mail").toString();

        Object rightCode = redisTemplate.opsForValue().get(mail);

        if (code != null && code.equals(rightCode)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getMail,mail);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setMail(mail);
                user.setName("用户"+rightCode);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

//            若登录成功，删除记录
            redisTemplate.delete(mail);
            return GeneralResult.success(user);
        }

        return GeneralResult.error("登录失败");
    }


    @PostMapping("/loginout")
    public GeneralResult<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return GeneralResult.success("退出成功");
    }
}
