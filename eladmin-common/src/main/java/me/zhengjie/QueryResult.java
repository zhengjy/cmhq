package me.zhengjie;

import lombok.Data;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/5 9:58.
 */
@Data
public class QueryResult<T> {
    private long total;
    private List<T> items;

    public QueryResult() {
    }

    public QueryResult(long total, List<T> items) {
        this.total = total;
        this.items = items;
    }
}
