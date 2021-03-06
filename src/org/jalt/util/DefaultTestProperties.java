package org.jalt.util;

import org.jalt.model.algorithm.stoppingcriterium.StopOnRMSError;

/**
 * 
 * @author andvicoso
 */
public final class DefaultTestProperties {

	public static final String FINAL_GOAL = "@";

	public static final int MAX_ITERATIONS = 100;

	public static final double BAD_REWARD = -100;// -30
	public static final double BAD_EXP_VALUE = BAD_REWARD / 2;

	public static final double GOOD_EXP_VALUE = -BAD_EXP_VALUE;
	public static final double GOOD_REWARD = -BAD_REWARD;

	public static final double OTHERWISE = -1;

	public static final double ALPHA = 0.1;
	public static final double EPSILON = 0.1;
	public static final double GAMA = 0.9;

	public static final double ERROR = 0.01;// 0.09(ERG) and 0.009(MDP)

	public static final StopOnRMSError DEFAULT_STOPON = new StopOnRMSError();// new StopOnMaxDiffError();//
}