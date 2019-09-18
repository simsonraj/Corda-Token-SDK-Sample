package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.google.common.collect.ImmutableList
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import com.r3.corda.lib.tokens.contracts.utilities.heldBy
import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.template.states.ExampleEvolvableTokenType
import net.corda.core.contracts.TransactionState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.ProgressTracker
import java.util.*
import com.r3.corda.lib.tokens.workflows.types.PartyAndToken
import net.corda.core.contracts.Amount
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.*
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.flows.FlowException
import net.corda.core.transactions.SignedTransaction
import net.corda.core.identity.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC





@StartableByRPC
class ExampleFlowWithEvolvableToken(
        val evolvableTokenId: String,
        val amount: Long,
        val recipient: Party
) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val uuid = UUID.fromString(evolvableTokenId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(uuid = listOf(uuid))
        val tokenStateAndRef = serviceHub.vaultService.queryBy<EvolvableTokenType>(queryCriteria).states.single()
        val token = tokenStateAndRef.state.data.toPointer<EvolvableTokenType>()
        // Starting this flow with a new flow session.
        val issueTokensFlow = IssueTokens(listOf(amount of token issuedBy ourIdentity heldBy recipient))
        return subFlow(issueTokensFlow)
    }
}

@StartableByRPC
class CreateExampleEvolvableToken(val data: String) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val evolvableTokenType = ExampleEvolvableTokenType(data, ourIdentity, linearId = UniqueIdentifier())
        val transactionState = TransactionState(evolvableTokenType, notary = notary)
        return subFlow(CreateEvolvableTokens(transactionState))
    }
}

@StartableByRPC
class MoveEvolvableFungibleTokenFlow(private val tokenId: String, private val holder: Party, private val quantity: Int) : FlowLogic<SignedTransaction>() {

    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        //get uuid from input tokenId
        val uuid = UUID.fromString(tokenId)
        //create criteria to get all unconsumed house states on ledger with uuid as input tokenId
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(uuid), null,
                Vault.StateStatus.UNCONSUMED, null)
        val tokenStateAndRef = serviceHub.vaultService.queryBy<EvolvableTokenType>(queryCriteria).states.single()
        val token = tokenStateAndRef.state.data.toPointer<EvolvableTokenType>()
        //specify how much amount to transfer to which holder
        val amount = Amount(quantity.toLong(), token)
        val partyAndAmount = PartyAndAmount(holder, amount)
        // Starting this flow with a new flow session.
        val moveTokensFlow = MoveFungibleTokens(quantity.toLong() of token, holder)
        return subFlow(moveTokensFlow)
    }
}

@StartableByRPC
class RedeemHouseFungibleTokenFlow(private val tokenId: String, private val issuer: Party, private val quantity: Int) : FlowLogic<SignedTransaction>() {

    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        //get uuid from input tokenId
        val uuid = UUID.fromString(tokenId)
        //create criteria to get all unconsumed house states on ledger with uuid as input tokenId
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(uuid), null,
                Vault.StateStatus.UNCONSUMED, null)
        val tokenStateAndRef = serviceHub.vaultService.queryBy<EvolvableTokenType>(queryCriteria).states.single()
        val token = tokenStateAndRef.state.data.toPointer<EvolvableTokenType>()
        val amount = Amount(quantity.toLong(), token)
        return subFlow(RedeemFungibleTokens(quantity.toLong() of token, issuer))
    }
}