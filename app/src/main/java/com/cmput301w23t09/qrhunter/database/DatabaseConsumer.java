package com.cmput301w23t09.qrhunter.database;

import java.util.function.Consumer;

/**
 * Utility interface as a shortcut to specifying a Consumer of a DatabaseQueryResult
 * @param <T> type the DatabaseQueryResults returns
 */
public interface DatabaseConsumer<T> extends Consumer<DatabaseQueryResults<T>> {
}
