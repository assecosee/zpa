package br.com.felipezorzo.sonar.plsql.checks;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.sonar.sslr.api.AstNode;

import br.com.felipezorzo.sonar.plsql.api.PlSqlGrammar;

@Rule(
    key = UselessParenthesisCheck.CHECK_KEY,
    priority = Priority.MINOR
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("2min")
@ActivatedByDefault
public class UselessParenthesisCheck extends BaseCheck {
    public static final String CHECK_KEY = "UselessParenthesis";
    
    @Override
    public void init() {
        subscribeTo(PlSqlGrammar.BRACKED_EXPRESSION);
    }
    
    @Override
    public void visitNode(AstNode node) {
        AstNode parent = node.getParent();
        if (parent.is(PlSqlGrammar.BRACKED_EXPRESSION) && 
            parent.getNumberOfChildren() == 3) {
            getContext().createLineViolation(this, getLocalizedMessage(CHECK_KEY), node);
        }
    }

}
