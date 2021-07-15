package WindClasses;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class RegisterWindAuctionService extends TickerBehaviour {

    public RegisterWindAuctionService(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(new WindAgent().getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("WindAuction");
        sd.setName(getAgent().getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(myAgent, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        stop();

    }
}
