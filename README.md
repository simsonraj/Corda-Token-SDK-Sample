<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# CorDapp Template - Kotlin

Welcome to the Kotlin CorDapp template. Integrated with Token_SDK to Create, Issue, Move and Redeem Tokens

**This is the Kotlin version of the CorDapp template. The Java equivalent is 
[here](https://github.com/corda/cordapp-template-java/).**

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html.

# Usage

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

Build and Deploy Nodes

    ./gradlew clean deployNodes
    ./build/nodes/runnodes

You can use Respective shells to interact with your node. 

   Create Tokens:
    
    start CreateExampleEvolvableToken data : 1

Run queryVault to Find the transaction ID and Use the ID in the subsequent commands

    run vaultQuery contractStateType : com.template.states.ExampleEvolvableTokenType

Issue Tokens:

    start ExampleFlowWithEvolvableToken evolvableTokenId : bc4f5dcb-27b5-484a-b366-19899ff34cbc , amount : 1 , recipient : PartyB

Move Tokens:

    start MoveEvolvableFungibleTokenFlow tokenId : bc4f5dcb-27b5-484a-b366-19899ff34cbc , holder : PartyA, quantity : 1

Redeem Tokens:

    start RedeemHouseFungibleTokenFlow tokenId : bc4f5dcb-27b5-484a-b366-19899ff34cbc, issuer : PartyA , quantity : 1 