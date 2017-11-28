/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ketal.webservice.jwt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTTokenUtil {

    private static final String USER = "username";
    private static final String ROLE = "roles";
    private final String secretKey;

    public JWTTokenUtil(String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(JWTPrincipal principal, Date expiration, Map<String, Object> claims) throws UnsupportedEncodingException {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(principal.getName())
                .setExpiration(expiration)
                .setIssuedAt(new Date())
                .claim(USER, principal.getName())
                .claim(ROLE, principal.getRoles())
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, this.secretKey.getBytes("UTF-8"));
        String jwtToken = jwtBuilder.compact();
        return jwtToken;
    }

    public JWTPrincipal validateToken(String jwtToken) throws Exception {
        Jws<Claims> claims = Jwts.parser().setSigningKey(this.secretKey.getBytes("UTF-8")).parseClaimsJws(jwtToken);
        Claims body = claims.getBody();
        
        if (new Date().after(body.getExpiration())) {
            throw new Exception("Token has expired.");
        }
        
        String username = body.getSubject();
        @SuppressWarnings("unchecked")
        ArrayList<String> roles = (ArrayList<String>) body.get(ROLE);
        
        return new JWTPrincipal(username, roles);
    }
    
    public Object getClaim(String jwtToken, Object keyName) throws Exception {
        Jws<Claims> claims = Jwts.parser().setSigningKey(this.secretKey.getBytes("UTF-8")).parseClaimsJws(jwtToken);
        Claims body = claims.getBody();
        
        return body.get(keyName);
    }
    
    public Map<String, Object> getClaims(String jwtToken, List<String> claimKeys) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        
        Jws<Claims> jwtClaims = Jwts.parser().setSigningKey(this.secretKey.getBytes("UTF-8")).parseClaimsJws(jwtToken);
        Claims body = jwtClaims.getBody();
        for(String key : claimKeys) {
            Object value;
            if((value = body.get(key)) != null) {
                claims.put(key, value);
            }
        }
        
        return claims;
    }
}
