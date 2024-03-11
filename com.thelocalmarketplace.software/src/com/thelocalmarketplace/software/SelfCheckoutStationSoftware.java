/** Talaal Irtija (UCID: 30169780)
 * Yotam Rojnov (UCID: 30173949)
 * Joseph Tandyo (UCID: 30182561) */



package com.thelocalmarketplace.software;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;

public class SelfCheckoutStationSoftware {

	/** Boolean variable that is used to track whether user interaction is blocked */
	private boolean blocked = false;

	/** Boolean variable to track if a current session is active or not. */
	private boolean active;

	/** Boolean variable to track whether the self checkout station is powered on. */
	private boolean enabled;

	/** Set function to change the blocked variable value
	 * @param value The new value for station block status*/
	public void setStationBlock(boolean value) {
		this.blocked = value;
	}

	/** Get function to get the blocked station status*/
	public boolean getStationBlock() {
		return this.blocked;
	}

	/** Function to start a session for self-checkout machine*/
	public void startSession() {

		// Check if there is a current active session.
		if (this.active) {
			throw new InvalidStateSimulationException("Session already started.");
		}

		// Reset all self-checkout station configurations to default.
		SelfCheckoutStation.resetConfigurationToDefaults();

		// Set the current session to active.
		this.active = true;

	}
}
