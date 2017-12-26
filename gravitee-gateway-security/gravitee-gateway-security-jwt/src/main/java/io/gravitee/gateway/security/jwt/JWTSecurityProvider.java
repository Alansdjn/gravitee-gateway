/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.security.jwt;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.policy.Policy;
import io.gravitee.gateway.security.core.AbstractSecurityProvider;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class JWTSecurityProvider extends AbstractSecurityProvider {

    static final String SECURITY_PROVIDER_JWT = "jwt";

    static final String JWT_POLICY = "jwt";

    static final String BEARER_AUTHENTICATION_TYPE = "Bearer";

    @Override
    public boolean canHandle(Request request) {
        List<String> authorizationHeaders = request.headers().get(HttpHeaders.AUTHORIZATION);

        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            return false;
        }


        Optional<String> authorizationBearerHeader = authorizationHeaders
                .stream()
                .filter(h -> StringUtils.startsWithIgnoreCase(h, BEARER_AUTHENTICATION_TYPE))
                .findFirst();

        if (! authorizationBearerHeader.isPresent()) {
            return false;
        }

        String accessToken = authorizationBearerHeader.get().substring(BEARER_AUTHENTICATION_TYPE.length()).trim();
        return ! accessToken.isEmpty();
    }

    @Override
    public String name() {
        return SECURITY_PROVIDER_JWT;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String configuration() {
        return null;
    }

    @Override
    public List<Policy> policies(ExecutionContext executionContext) {
        return Arrays.asList(
                // First, validate the incoming access_token thanks to an OAuth2 authorization server
                create(JWT_POLICY, configuration()),

                // Then, check that there is an existing subscription which is valid
                null);
    }
}
