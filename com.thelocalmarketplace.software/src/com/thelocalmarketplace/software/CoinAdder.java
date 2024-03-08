package com.thelocalmarketplace.software;


import java.math.BigDecimal;
import java.util.ArrayList;


import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;


public class CoinAdder extends SelfCheckoutStation{

	private SelfCheckoutStation cStation;
	private ArrayList<Coin> coinsList;
	private PaymentHandler handler;
	
		public CoinAdder(SelfCheckoutStation cStation, PaymentHandler handler) {
			if(cStation == null) throw new NullPointerException("No argument may be null.");
			this.cStation = cStation;
			this.handler = handler;
		}
		
		/**
		 * Inserts a machine into the coin slot and adds it to a list of accepted coins.
		 * 
		 * @param coin The coin to be inserted into the system
		 * @return true if the coin was accepted into machine and added to coinsList, false otherwise.
		 * @throws DisabledException If the coin slot is disabled
		 * @throws CashOverloadException If the coin storage is overloaded
		 */
		public boolean insertCoin(Coin coin) throws DisabledException, CashOverloadException {
			BigDecimal zero = BigDecimal.ZERO;
			BigDecimal amountLeft = handler.getChangeRemaining();
			int compareResult = amountLeft.compareTo(zero);
			if(compareResult == 1) {
				if(coin == null) 
					throw new NullPointerException("coin cannot be null."); // Check for null parameters.
				boolean successfulInsertion = acceptInsertedCoin(coin);
				if (successfulInsertion) {
					coinsList.add(coin);
					return true;
				}
				return false;
			}
			return false;
		}
		
		/**
		 * Get the list of coins that were successfully added to the machine
		 * @return the list of accepted coins
		 */
		public ArrayList<Coin> getAcceptedCoinsList() {
			return coinsList;
		}
		
		/**
		 * Accepts a coin inserted by the customer into the coin slot.
		 * 
		 * @param coin The coin to be validated and accepted.
		 * @return true if the coin is successfully accepted, false otherwise.
		 * @throws DisabledException If the coin slot is disabled.
		 * @throws CashOverloadException If the coin storage is overloaded.
		 */
		private boolean acceptInsertedCoin(Coin coin) throws DisabledException, CashOverloadException { 
			if(this.cStation.coinStorage.hasSpace()) {
				this.cStation.coinSlot.receive(coin);
			} else {
				this.cStation.coinSlot.disable();
			}
			return false;
		}
		
}
