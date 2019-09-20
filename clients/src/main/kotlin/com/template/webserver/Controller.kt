package com.template.webserver



import net.corda.core.messaging.startTrackedFlow
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

import com.template.flows.ExampleFlowWithEvolvableToken
import com.template.states.ExampleEvolvableTokenType
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.*
import net.corda.core.utilities.getOrThrow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/templateendpoint", produces = arrayOf("text/plain"))
    private fun templateendpoint(): String {
        return "Define an endpoint here."
    }



    @PostMapping(value = [ "create-tokens" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun createTokens(request: HttpServletRequest): ResponseEntity<String> {
        val data = request.getParameter("informationThatMayChange").trim()
        //val date = LocalDate.parse(request.getParameter("date").substringBefore("00").trim(), DateTimeFormatter.ofPattern("E MMMM d yyyy"))
        //val partyName = request.getParameter("megacorp") ?: return ResponseEntity.badRequest().body("Query parameter 'MegaCorp' must not be null.\n")
       /* if (hoursWorked <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'hoursWorked' must be non-negative.\n")
        }
        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n")
*/
        return try {
            val signedTx = proxy.startTrackedFlow(ExampleFlowWithEvolvableToken::CreateExampleEvolvableToken, data).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }
/*
    @GetMapping(value = "states", produces = [ TEXT_PLAIN_VALUE ])
    private fun states(): ResponseEntity<String>  {
        val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)
        val paging = PageSpecification(1, 10)
        val sorting = Sort(setOf(Sort.SortColumn(SortAttribute.Standard(Sort.VaultStateAttribute.RECORDED_TIME), Sort.Direction.DESC)))
        return ResponseEntity.status(HttpStatus.CREATED).body(proxy.vaultQueryBy<ExampleEvolvableTokenType>(criteria,paging,sorting).states.toString())
    }*/

    @GetMapping(value = [ "states" ], produces = [ APPLICATION_JSON_VALUE ])
    fun getMyIOUs(): ResponseEntity<List<UniqueIdentifier>>? {
       //val myious = proxy.vaultQueryBy<ExampleEvolvableTokenType>().states.filter { it.state.data.maintainer.equals(proxy.nodeInfo().legalIdentities.first()) }
        val myious = proxy.vaultQueryBy<ExampleEvolvableTokenType>().states.filter { it.state.data.maintainer.equals(proxy.nodeInfo().legalIdentities.first()) }
        val linearIds = myious.map { it.state.data.linearId }.toList()
        logger.error("responsee haiii"+linearIds.toString())
        val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)
        val results = proxy.vaultQueryBy<LinearState>(criteria).states
       return ResponseEntity.ok(linearIds)
    }

    /**
     * Displays all IOU states that exist in the node's vault.
     */
  /*  @GetMapping(value = [ "invoices" ], produces = [ APPLICATION_JSON_VALUE ])
    fun getInvoices() : ResponseEntity<List<StateAndRef<InvoiceState>>> {
        return ResponseEntity.ok(proxy.vaultQueryBy<InvoiceState>().states)
    }*/
/*
    @PostMapping(value = [ "Issue-tokens" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun issueTokens(request: HttpServletRequest): ResponseEntity<String> {
        val data = request.getParameter("informationThatMayChange").trim()
        //val date = LocalDate.parse(request.getParameter("date").substringBefore("00").trim(), DateTimeFormatter.ofPattern("E MMMM d yyyy"))
        //val partyName = request.getParameter("megacorp") ?: return ResponseEntity.badRequest().body("Query parameter 'MegaCorp' must not be null.\n")
        /* if (hoursWorked <= 0 ) {
             return ResponseEntity.badRequest().body("Query parameter 'hoursWorked' must be non-negative.\n")
         }
         val partyX500Name = CordaX500Name.parse(partyName)
         val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n")
 */
        return try {
            val signedTx = proxy.startTrackedFlow(ExampleFlowWithEvolvableToken::CreateExampleEvolvableToken, data).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }*/
}