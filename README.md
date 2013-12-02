BTCExamples
===========

Hello, how are you?

There are 2 tests in this example so far.

test 1) in bitsurance (legacy name) package. This test is just for testing that you can really comunicate with the 
testnet network. The test is doing the following:
1) syncs chain 
2) waits for a transaction to come into the wallet
3) sends received money back to faucet 

If you would run it without doing anything else, it will not top. search the log for string "Keys:" and find address 
Send some coins (like 0.01) from a test bitcoin faucet to this address.



test2) in escrew package this tests are trying to simulate escrow transaction. First run the sync test case and go play 
some game in the meanwhile. It will take time to sync (like 20 minutes i guess).

After the sync testcase finishishes, you can try to play with the other testcase. It expects that there are some coins
in the buyers wallet. Then creates the Escrow transaction and another transaction to spend the escrow money.

