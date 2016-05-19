/*
 * Sonar PL/SQL Plugin (Community)
 * Copyright (C) 2015-2016 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen.matchers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;

public class MethodMatcher {

    private NameCriteria methodName;
    private NameCriteria packageName;
    private NameCriteria schemaName;
    private int parameterCount;
    private boolean shouldCheckParameters = true;
    private boolean schemaIsOptional = false;

    private MethodMatcher() {
        // instances should be created using the create method
    }
    
    public static MethodMatcher create() {
        return new MethodMatcher();
    }
    
    public MethodMatcher name(String methodName) {
        return name(NameCriteria.is(methodName));
    }
    
    public MethodMatcher name(NameCriteria methodName) {
        Preconditions.checkState(this.methodName == null);
        this.methodName = methodName;
        return this;
    }
    
    public MethodMatcher packageName(String packageName) {
        return packageName(NameCriteria.is(packageName));
    }
    
    public MethodMatcher packageName(NameCriteria packageName) {
        Preconditions.checkState(this.packageName == null);
        this.packageName = packageName;
        return this;
    }
    
    public MethodMatcher schema(String schemaName) {
        return schema(NameCriteria.is(schemaName));
    }
    
    public MethodMatcher schema(NameCriteria schemaName) {
        Preconditions.checkState(this.schemaName == null);
        this.schemaName = schemaName;
        return this;
    }
    
    public MethodMatcher withNoParameterConstraint() {
        Preconditions.checkState(this.parameterCount == 0);
        this.shouldCheckParameters = false;
        return this;
    }

    public MethodMatcher schemaIsOptional() {
        this.schemaIsOptional = true;
        return this;
    }
    
    public MethodMatcher addParameter() {
        Preconditions.checkState(this.shouldCheckParameters);
        this.parameterCount++;
        return this;
    }
    
    public List<AstNode> getArguments(AstNode node) {
        AstNode arguments = node.getFirstChild(PlSqlGrammar.ARGUMENTS);
        if (arguments != null) {
            return arguments.getChildren(PlSqlGrammar.ARGUMENT);
        }
        
        return new ArrayList<>();
    }

    public boolean matches(AstNode originalNode) {
        AstNode node = normalize(originalNode);
        LinkedList<AstNode> nodes = Lists.newLinkedList(node.getChildren(PlSqlGrammar.VARIABLE_NAME, PlSqlGrammar.IDENTIFIER_NAME));
        
        if (nodes.size() == 0) {
            return false;
        }
        
        boolean matches = true;
        
        matches &= nameAcceptable(nodes.removeLast(), methodName);
        
        if (packageName != null) {
            matches &= !nodes.isEmpty() && nameAcceptable(nodes.removeLast(), packageName);
        }
        
        if (schemaName != null) {
            matches &= (schemaIsOptional && nodes.isEmpty()) || 
                    (!nodes.isEmpty() && nameAcceptable(nodes.removeLast(), schemaName));
        }
        
        return matches && nodes.isEmpty() && argumentsAcceptable(originalNode);
    }

    private static boolean nameAcceptable(AstNode node, NameCriteria criteria) {
        String name = node.getTokenOriginalValue();
        return criteria.matches(name);
    }

    private boolean argumentsAcceptable(AstNode node) {
        return !shouldCheckParameters || getArguments(node).size() == parameterCount;
    }
    
    private static AstNode normalize(AstNode node) {
        if (node.is(PlSqlGrammar.METHOD_CALL, PlSqlGrammar.CALL_STATEMENT)) {
            AstNode child = normalize(node.getFirstChild());
            if (child.getFirstChild().is(PlSqlGrammar.HOST_AND_INDICATOR_VARIABLE)) {
                child = child.getFirstChild();
            }
            return child;
        }
        return node;
    }
    
}
