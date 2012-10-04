package org.emast.model.agent.factory;

import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface AgentFactory<M extends MDP> {

    Agent create(int pAgentIndex);

    List<Agent> createAgents(int pAgents);
}
