/*
 * Copyright (c) 2013 ITOCHU Techno-Solutions Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.ctc_g.jse.core.rest.jersey.exception.mapper.test_resource;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/error")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientErrorExceptionResourceTest {

    @GET
    @Path("/401")
    public EmptyTest error401() {
        throw new ClientErrorException(Response.Status.UNAUTHORIZED);
    }

    @GET
    @Path("/407")
    public EmptyTest error407() {
        throw new ClientErrorException(Response.Status.PROXY_AUTHENTICATION_REQUIRED);
    }

    @GET
    @Path("/408")
    public EmptyTest error408() {
        throw new ClientErrorException(Response.Status.REQUEST_TIMEOUT);
    }

    @GET
    @Path("/422")
    public EmptyTest error422() {
        throw new ClientErrorException(422);
    }

    @GET
    @Path("/417")
    public EmptyTest error417() {
        throw new ClientErrorException(Response.Status.EXPECTATION_FAILED);
    }

}
