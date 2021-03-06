/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.common.dto.convert.rule;

import org.dromara.soul.common.dto.convert.rule.impl.ContextMappingHandle;
import org.dromara.soul.common.dto.convert.rule.impl.DivideRuleHandle;
import org.dromara.soul.common.dto.convert.rule.impl.DubboRuleHandle;
import org.dromara.soul.common.dto.convert.rule.impl.SofaRuleHandle;
import org.dromara.soul.common.dto.convert.rule.impl.SpringCloudRuleHandle;
import org.dromara.soul.common.enums.PluginEnum;
import org.dromara.soul.common.exception.SoulException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The RuleHandle factory.
 *
 * @author yiwenlong (wlong.yi@gmail.com)
 */
public final class RuleHandleFactory {

    /**
     * The RpcType to RuleHandle class map.
     */
    private static final Map<String, Class<? extends RuleHandle>> RPC_TYPE_TO_RULE_HANDLE_CLASS = new ConcurrentHashMap<>();

    /**
     * The default RuleHandle.
     */
    private static final Class<? extends RuleHandle> DEFAULT_RULE_HANDLE = SpringCloudRuleHandle.class;

    static {
        RPC_TYPE_TO_RULE_HANDLE_CLASS.put(PluginEnum.DIVIDE.getName(), DivideRuleHandle.class);
        RPC_TYPE_TO_RULE_HANDLE_CLASS.put(PluginEnum.DUBBO.getName(), DubboRuleHandle.class);
        RPC_TYPE_TO_RULE_HANDLE_CLASS.put(PluginEnum.SOFA.getName(), SofaRuleHandle.class);
        RPC_TYPE_TO_RULE_HANDLE_CLASS.put(PluginEnum.SPRING_CLOUD.getName(), SpringCloudRuleHandle.class);
        RPC_TYPE_TO_RULE_HANDLE_CLASS.put(PluginEnum.CONTEXTPATH_MAPPING.getName(), ContextMappingHandle.class);
    }

    /**
     * Get a RuleHandle object with given rpc type and path.
     * @param name   name.
     * @param path      path.
     * @return          RuleHandle object.
     */
    public static RuleHandle ruleHandle(final String name, final String path) {
        Class<? extends RuleHandle> clazz = RPC_TYPE_TO_RULE_HANDLE_CLASS.getOrDefault(name, DEFAULT_RULE_HANDLE);
        try {
            return clazz.newInstance().createDefault(path);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SoulException(
                    String.format("Init RuleHandle failed with plugin name: %s, rule class: %s, exception: %s",
                            name,
                            clazz.getSimpleName(),
                            e.getMessage()));
        }
    }
}
