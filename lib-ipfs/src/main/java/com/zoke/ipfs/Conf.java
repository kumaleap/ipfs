package com.zoke.ipfs;

/**
 * Created by wulijie on 2018/6/6.
 */
public interface Conf {
    boolean DEBUG = true;
    String IPFS_RPC = "https://ipfs.infura.io:5001";
    String IPFS_GATEWAY = "https://ipfs.infura.io";
    String IPFS_RPC_ADD = IPFS_RPC.concat("/api/v0/add?stream-channels=true");
}
