/**
 * Yotam Rojnov (UCID: 30173949)
 * Duncan McKay (UCID: 30177857)
 * Mahfuz Alam (UCID:30142265)
 * Luis Trigueros Granillo (UCID: 30167989)
 * Lilia Skumatova (UCID: 30187339)
 * Abdelrahman Abbas (UCID: 30110374)
 * Talaal Irtija (UCID: 30169780)
 * Alejandro Cardona (UCID: 30178941)
 * Alexandre Duteau (UCID: 30192082)
 * Grace Johnson (UCID: 30149693)
 * Abil Momin (UCID: 30154771)
 * Tara Ghasemi M. Rad (UCID: 30171212)
 * Izabella Mawani (UCID: 30179738)
 * Binish Khalid (UCID: 30061367)
 * Fatima Khalid (UCID: 30140757)
 * Lucas Kasdorf (UCID: 30173922)
 * Emily Garcia-Volk (UCID: 30140791)
 * Yuinikoru Futamata (UCID: 30173228)
 * Joseph Tandyo (UCID: 30182561)
 * Syed Haider (UCID: 30143096)
 * Nami Marwah (UCID: 30178528)
 */
package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScale;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.NoPowerException;
import powerutility.PowerGrid;


public class WeightDiscrepancyTest {         
	
	private WeightDiscrepancy weightDiscrepancy;
	private Order order;
	private ElectronicScale scale;
	private WeightDiscrepancy weightDiscrepancyla;
	private Order order2;
	private ElectronicScale scale2;
	private WeightDiscrepancy weightDiscrepancy33;
	private Order order3;
	private ElectronicScale scale3;
	
     
     
    
	@Before

	public void setUp() throws OverloadedDevice {   
		 	
			
	        scale = new ElectronicScale();
	        PowerGrid grid = PowerGrid.instance();
	        scale.plugIn(grid);
	        scale.turnOn();
	        scale.enable();
	        order = new Order(scale);
	        weightDiscrepancy = new WeightDiscrepancy(order, scale);     
	          

    }
    
	@After
    public void tearDown() {
        
        // Unblock the system
        SelfCheckoutStationSoftware.setStationBlock(false);
    }
	
	  
	
	
    class MockItem extends Item {
        public MockItem(Mass mass) {
            super(mass);
        }

    }   
    
    

    
    @Test
    public void testUpdateMass_AddItemToOrder() throws OverloadedDevice {
         
    	 MockItem item1 = new MockItem(new Mass(100));
    	 MockItem item2 = new MockItem(new Mass(100));   
    	    
        
        order.addItemToOrder(item1);
        order.addItemToOrder(item2);
        
        
 
        WeightDiscrepancy weightDiscrepancy2 = new WeightDiscrepancy(order,scale);
        
        weightDiscrepancy2.updateMass();

        Mass expectedMass = new Mass(200);
 
       
        assertEquals(expectedMass, scale.getCurrentMassOnTheScale()); 
        
        
    } 
    
    
    
    
    

    @Test
    public void testcheckDiscrepancy_diff() throws OverloadedDevice {
    	
    	scale3 = new ElectronicScale();
        PowerGrid grid = PowerGrid.instance();
        scale3.plugIn(grid);
        scale3.turnOn();
        scale3.enable();
        order3 = new Order(scale3);
        weightDiscrepancy33 = new WeightDiscrepancy(order3, scale3);   
    	
    	
    	 MockItem item1 = new MockItem(new Mass(100));
         MockItem item2 = new MockItem(new Mass(150));
    	
         order3.addItemToOrder(item1);
         order3.addItemToOrder(item2);
         scale3.addAnItem(item1);
         
        
        weightDiscrepancy33.checkDiscrepancy(); 
        
        assertTrue(SelfCheckoutStationSoftware.getStationBlock());     
        
            }
    
    
    @Test
    public void testcheckDiscrepancy_same() throws OverloadedDevice {
    
    	 	scale2 = new ElectronicScale();
	        PowerGrid grid = PowerGrid.instance();
	        scale2.plugIn(grid);
	        scale2.turnOn();
	        scale2.enable();
	        order2 = new Order(scale2);
	        weightDiscrepancyla = new WeightDiscrepancy(order2, scale2);         
    	
    	 MockItem item1 = new MockItem(new Mass(100));
         
    	
         order2.addItemToOrder(item1);
        
         scale2.addAnItem(item1);      
       
         
        WeightDiscrepancy weightDiscrepancy3 = new WeightDiscrepancy(order2,scale2);
         
        weightDiscrepancy3.checkDiscrepancy();
        
        assertEquals(new Mass(100),scale2.getCurrentMassOnTheScale());
    
        
        //assertFalse(SelfCheckoutStationSoftware.getStationBlock());     
        
    }                
    
    
    
    
    // weight at block - scale
    // current weight - order
    
    
    @Test
    public void testCheckRemoval_greaterthan() throws OverloadedDevice {
    	
            
        MockItem item1 = new MockItem(new Mass(100));
        MockItem item2 = new MockItem(new Mass(1000));
        MockItem item3 = new MockItem(new Mass(6000));
        
        order.addItemToOrder(item1); 
        order.addItemToOrder(item2);
        order.addItemToOrder(item3);
        
        scale.addAnItem(item1);
            
        
        weightDiscrepancy.checkDiscrepancy(); 
        
        assertFalse(weightDiscrepancy.checkRemoval());    
    }  

 
    @Test
    public void testCheckRemoval_lessthan() throws OverloadedDevice {
        MockItem item1 = new MockItem(new Mass(1000));
        MockItem item2 = new MockItem(new Mass(10));
        MockItem item3 = new MockItem(new Mass(60));
        
        order.addItemToOrder(item1); 
        order.addItemToOrder(item2);
        order.addItemToOrder(item3);
        
        scale.addAnItem(item1);  
        weightDiscrepancy.checkDiscrepancy();       
        assertTrue(weightDiscrepancy.checkRemoval());      
    }  
  
  @Test
  public void testCheckbaggage_greaterthan() {
      
      MockItem item1 = new MockItem(new Mass(100));
      MockItem item2 = new MockItem(new Mass(1000));
      MockItem item3 = new MockItem(new Mass(6000));
      
      order.addItemToOrder(item1); 
      order.addItemToOrder(item2);
      order.addItemToOrder(item3);
      
      scale.addAnItem(item1);      
      weightDiscrepancy.checkBaggageAddition();   
      assertTrue(weightDiscrepancy.checkBaggageAddition()); 
  }  
  
  
  @Test
  public void testCheckbaggage_lesfsthan() {
 
      MockItem item1 = new MockItem(new Mass(1000));
      MockItem item2 = new MockItem(new Mass(10));
      MockItem item3 = new MockItem(new Mass(60));
      
      order.addItemToOrder(item1); 
      order.addItemToOrder(item2);
      order.addItemToOrder(item3);
      
      scale.addAnItem(item1);
  
      weightDiscrepancy.checkBaggageAddition();
      assertFalse(weightDiscrepancy.checkBaggageAddition());       
      
  }  
  

  
  @Test
  public void checkWeightChangeTestTrue(){
     
      Mass mass1 = new Mass(500);
      MockItem item1 = new MockItem(mass1); 
      order.addItemToOrder(item1);
      order.addTotalWeightInGrams(5);
      assertTrue(weightDiscrepancy.checkWeightChange());    
  }
  
	@Test
  public void checkWeightChangeTestFalse() throws Exception{
     
  	order.addTotalWeightInGrams(0.5);  //order.getTotalWeightInGrams();
  	
      Mass mass1 = new Mass(500000);
      MockItem item1 = new MockItem(mass1); 
      scale.addAnItem(item1); // scale.getCurrentMassOnTheScale() = 500 000 mcg
      assertFalse(weightDiscrepancy.checkWeightChange());    
      assertEquals(scale.getCurrentMassOnTheScale(), order.getTotalWeightInGrams());
      
  } 

	
	
	
	
	// mass changes and session is not blocked
	@Test
	public void notifymasschange_blocked() {
		MockItem item1 = new MockItem(new Mass(1000));
		order.addItemToOrder(item1); 
      scale.addAnItem(item1);
      weightDiscrepancy.notifyMassChanged();
      
	}
  
	
	@Test
	public void notifymasschange_unblocked() {
		
	}

}
    
