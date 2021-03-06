/*
Copyright (c) 2008 Health Market Science, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.healthmarketscience.sqlbuilder;

import com.healthmarketscience.common.util.AppendableExt;

import java.io.IOException;

/**
 * Query which generates a REVOKE (privileges) statement.
 *
 * @author James Ahlborn
 */
public class RevokeQuery extends BaseGrantQuery<RevokeQuery> {

    private DropQuery.Behavior _behavior;

    public RevokeQuery() {
    }

    /**
     * Sets the behavior for the revoke query
     */
    public RevokeQuery setBehavior(DropQuery.Behavior newBehavior) {
        _behavior = newBehavior;
        return this;
    }

    @Override
    protected void appendTo(AppendableExt app, SqlContext newContext)
            throws IOException {
        newContext.setUseTableAliases(false);

        app.append("REVOKE ").append(_privileges).append(" ON ")
                .append(_targetObj).append(" FROM ").append(_grantees);
        if (_behavior != null) {
            app.append(_behavior);
        }
    }

}
