package com.example.anurag.anurag_chat_app;

/**
 * Created by anurag on 8/4/18.
 */

public class Conv {
    public boolean seen;
    public long timestamp;
    public Conv()
    {

    }
    public boolean isSeen()
    {
        return seen;

    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public Conv(boolean seen,long timestamp)
    {
        this.seen=seen;
        this.timestamp=timestamp;
    }
}
