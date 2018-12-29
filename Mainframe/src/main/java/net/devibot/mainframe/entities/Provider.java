package net.devibot.mainframe.entities;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devibot.grpc.provider.ProviderServiceGrpc;
import net.devibot.mainframe.Mainframe;

public class Provider {

    private Mainframe mainframe;

    private String ip;
    private int port;

    private int id;
    private long lastKeepAliveTime = 0L;

    ProviderServiceGrpc.ProviderServiceStub stub;

    public Provider(String ip, int port, int id, Mainframe mainframe) {
        this.ip = ip;
        this.port = port;
        this.id = id;

        this.mainframe = mainframe;

        this.stub =  ProviderServiceGrpc.newStub(ManagedChannelBuilder.forAddress(this.ip, this.port).usePlaintext().executor(mainframe.getThreadPool()).build());
    }

    public ProviderServiceGrpc.ProviderServiceStub getStub() {
        return stub;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public long getLastKeepAliveTime() {
        return lastKeepAliveTime;
    }

    public void setLastKeepAliveTime() {
        this.lastKeepAliveTime = System.currentTimeMillis();
    }
}
