package ConsumerClasses;

import PVClasses.PVAgent;
import WindClasses.WindAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class SearchingAuctionService extends TickerBehaviour {

    // Ставка от потребителя
    private double bet = 50000 + Math.random() * 50000;


    public SearchingAuctionService(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        searchingAgents(myAgent, new WindAgent(),"WindAuction");
        searchingAgents(myAgent, new PVAgent(), "PVAuction");
//        stop();
    }

    private void searchingAgents(Agent  consumer, Agent found, String type){

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(found.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);


        try {
            DFAgentDescription[] result = DFService.search(consumer, dfd);
            for (int i = 0; i < result.length; i++) {
//                System.out.println(myAgent.getLocalName() + " found "
//                        + result[i].getName().getLocalName());

                String foundAgent = result[i].getName().getLocalName();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                AID aid = new AID(foundAgent, false);
                msg.addReceiver(aid);
                msg.setProtocol("HelloFromConsumers");
                msg.setContent("Hello");
                consumer.send(msg);
//                System.out.println(myAgent.getLocalName() + " send 'hello' to " +
//                        foundAgent);
            }
        }catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
