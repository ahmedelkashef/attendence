package com.cloudypedia.endpoint;



import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;


/**
 * Defines conference APIs.
 */

@Api(
        name = "Fawry Api",
        version = "v1"
        
)

public class FawryApi {

    @ApiMethod(name = "GetNearestLocation", path = "GetNearestLocation", httpMethod = HttpMethod.GET)
    public List<Object> getData() throws UnauthorizedException {
       
    }

    
}
