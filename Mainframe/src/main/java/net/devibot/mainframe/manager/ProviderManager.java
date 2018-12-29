package net.devibot.mainframe.manager;

import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.entities.Provider;

import java.util.ArrayList;
import java.util.List;

public class ProviderManager {

    private Mainframe mainframe;

    private List<Provider> providerList = new ArrayList<>();

    public ProviderManager(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    public boolean isProviderNeeded() {
        int connected = providerList.size();
        int needed = 1; //todo figure out amount of needed provider based on guild count
        return connected < needed;
    }

    public Provider connectProvider(String ip, int port) {
        if (!isProviderNeeded())
            return null;
        Provider provider = new Provider(ip, port,providerList.size() + 1, mainframe);
        providerList.add(provider);

        return provider;
    }

    public List<Provider> getProviderList() {
        return providerList;
    }
}
