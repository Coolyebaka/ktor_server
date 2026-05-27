package com.huntersdiary.rules.presentation

import com.huntersdiary.rules.domain.GetRuleByIdUseCase
import com.huntersdiary.rules.domain.GetRulesUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.ruleRoutes(
    getRulesUseCase: GetRulesUseCase,
    getRuleByIdUseCase: GetRuleByIdUseCase,
) {
    route("/rules") {
        get {
            val query = call.request.queryParameters["query"]
            val rules = getRulesUseCase.execute(query).map { it.toResponse() }

            call.respond(rules)
        }

        get("/{id}") {
            val ruleId = call.parameters["id"].orEmpty()
            val rule = getRuleByIdUseCase.execute(ruleId)

            call.respond(rule.toResponse())
        }
    }
}
