/**
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2021 Felipe Zorzo
 * mailto:felipe AT felipezorzo DOT com DOT br
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
package org.sonar.plugins.plsqlopen.api.statements

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar
import org.sonar.plugins.plsqlopen.api.RuleTest
import org.sonar.sslr.tests.Assertions.assertThat

class OpenStatementTest : RuleTest() {

    @BeforeEach
    fun init() {
        setRootRule(PlSqlGrammar.OPEN_STATEMENT)
    }

    @Test
    fun matchesSimpleOpen() {
        assertThat(p).matches("open cur;")
    }

    @Test
    fun matchesOpenCursorInPackage() {
        assertThat(p).matches("open pack.cur;")
    }

    @Test
    fun matchesOpenWithParameter() {
        assertThat(p).matches("open cur(foo);")
    }

    @Test
    fun matchesOpenWithMultipleParameters() {
        assertThat(p).matches("open cur(foo, bar);")
    }

    @Test
    fun matchesOpenWithNamedParameters() {
        assertThat(p).matches("open cur(x => foo, y => bar);")
    }

    @Test
    fun matchesLabeledOpen() {
        assertThat(p).matches("<<foo>> open cur;")
    }

}
