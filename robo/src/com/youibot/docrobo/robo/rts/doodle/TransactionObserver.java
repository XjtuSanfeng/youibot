package com.youibot.docrobo.robo.rts.doodle;

import java.util.List;

/**
 * Created by huangjun on 2015/6/24.
 */
public interface TransactionObserver {
    void onTransaction(List<Transaction> transactions);
}
