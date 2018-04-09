/* UserUtil
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.Util;

import com.example.dada.Model.User;
import com.google.gson.Gson;

/**
 * Utility class that help to serilize and deserialize the user object
 */
public class UserUtil {

    /**
     * serializer user
     * @param user User
     * @return String user
     */
    public static String serializer(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    /**
     * change string user to User
     * @param string json.string
     * @return user
     */
    public static User deserializer(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, User.class);
    }
}