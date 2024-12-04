package org.bitcoindevkit.devkitwallet.domain

import android.util.Log
import org.bitcoindevkit.NodeEventHandler
import org.bitcoindevkit.NodeState
import org.bitcoindevkit.Warning
import org.rustbitcoin.bitcoin.Txid

class KyotoNodeEventHandler(
    val triggerSnackbar: (message: String) -> Unit,
    val updateLatestBLock: (blockHeight: UInt) -> Unit,
) : NodeEventHandler {
    override fun blocksDisconnected(blocks: List<UInt>) {
        Log.i("KyotoNodeEvent", "Blocks disconnected: $blocks")
    }

    override fun connectionsMet() {
        Log.i("KyotoNodeEvent", "Connections met")
    }

    override fun dialog(dialog: String) {
        Log.i("KyotoNodeEvent", "Dialog: $dialog")
        // if (dialog.contains("peer height")) {
        //     val height = dialog.split("peer height: ")[1].split(" ")[0]
        //     Log.i("KyotoNodeEvent", "Peer height from the dialog method: $height")
        //     triggerSnackbar("New block mined: $height")
        // }
    }

    override fun stateChanged(state: NodeState) {
        Log.i("KyotoNodeEvent", "State changed: $state")
    }

    override fun synced(tip: UInt) {
        Log.i("KyotoNodeEvent", "Synced: $tip")
        triggerSnackbar("Synced to block $tip")
        updateLatestBLock(tip)
    }

    override fun txFailed(txid: Txid) {
        Log.i("KyotoNodeEvent", "Tx failed: $txid")
    }

    override fun txSent(txid: Txid) {
        Log.i("KyotoNodeEvent", "Tx sent: $txid")
    }

    override fun warning(warning: Warning) {
        Log.i("KyotoNodeEvent", "Warning: $warning")
    }
}
