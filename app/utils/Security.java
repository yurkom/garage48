package utils;

import command.CommandEnum;
import model.bean.Member;
import javax.servlet.http.HttpServletRequest;

public class Security {

    public static final String PARAM_USER = "user";

    public static boolean checkPermission(User user, HttpServletRequest request){

        User user = (User) request.getSession().getAttribute(PARAM_USER);

        switch (user.getUserType()){
            case TRAINER:
                if (user != null){
                    if (user.isTrainer()){
                        return true;
                    }
                } break;
            case PLAYER:
                if (user != null){
                    if (!member.isTrainer()){
                        return true;
                    }
                }break;
            case ALL: return true;
        }
        return false;
    }
}