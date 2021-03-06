// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns whether a user is logged in or not and a URL to login/out */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
  
  private static final Gson gson = new Gson();
  private static final String URL_REDIRECT_AFTER_LOG_OUT = "/";
  private static final String URL_REDIRECT_AFTER_LOG_IN = "/";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      boolean isUserLoggedIn = userService.isUserLoggedIn();

      Map<String, String> loginStatus = new HashMap<String, String>();
      loginStatus.put("isUserLoggedIn", String.valueOf(isUserLoggedIn));
      
      if (isUserLoggedIn) {
        loginStatus.put("email", userService.getCurrentUser().getEmail());
        loginStatus.put("url", userService.createLogoutURL(URL_REDIRECT_AFTER_LOG_OUT));
      } else {
        loginStatus.put("email", null);
        loginStatus.put("url", userService.createLoginURL(URL_REDIRECT_AFTER_LOG_IN));
    }

    String json = gson.toJson(loginStatus);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
} 
