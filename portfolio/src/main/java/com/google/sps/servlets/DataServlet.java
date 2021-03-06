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

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.*;
import java.io.IOException;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments from datastore*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
 
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    
    int commentLimit = getCommentLimit(request);
    List<Comment> comments = new ArrayList<Comment>();
    for(Entity entity : results.asIterable(FetchOptions.Builder.withLimit(commentLimit))){
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String message = (String) entity.getProperty("message");
      long timestamp = (long) entity.getProperty("timestamp");
      String mood = (String) entity.getProperty("mood");
      String email = (String) entity.getProperty("email");

      Comment comment = new Comment(id, name, message, timestamp, mood, email);
      comments.add(comment);
    }

    Gson gson = new Gson();
    String json = gson.toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String message = request.getParameter("message");
    long timestamp = System.currentTimeMillis();
    String mood = request.getParameter("mood");
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("mood", mood);
    commentEntity.setProperty("email", email);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    if(!("").equals(message) && !("").equals(name)){
      datastore.put(commentEntity);
    }
    response.sendRedirect("/index.html");
  }

  /** Returns the comment limit entered by the user, or 5 as default */
  private int getCommentLimit(HttpServletRequest request){
    String commentLimitStr = request.getParameter("comment-limit");

    int commentLimit = 5; //default
    commentLimit = Integer.parseInt(commentLimitStr);
    return commentLimit;
  }
 
}

