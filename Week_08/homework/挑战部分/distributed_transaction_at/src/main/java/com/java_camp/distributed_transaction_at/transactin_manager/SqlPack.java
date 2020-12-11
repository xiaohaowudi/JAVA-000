package com.java_camp.distributed_transaction_at.transactin_manager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SqlPack {
    String sql;
    String primaryKey;
}
