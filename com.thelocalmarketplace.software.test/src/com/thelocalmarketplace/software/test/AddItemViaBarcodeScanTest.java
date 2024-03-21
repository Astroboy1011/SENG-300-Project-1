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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.AddItemViaBarcodeScan;
import com.thelocalmarketplace.software.BaggingAreaListener;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.hardware_0.2.3.

import powerutility.PowerGrid;

public class AddItemViaBarcodeScanTest {
	PowerGrid grid;
	// this needs to be changed according to the description, as we now have new classes, BarcodeScannerBronze, BarcodeScannerGold, BarcodeScannerSilver
	BarcodeScanner scanner;
	// Same goes for the electronicScale (gold, silver, bronze)
	ElectronicScale baggingArea;					
	WeightDiscrepancy weightDiscrepancy;
	BarcodedItem barcodedItem;
	BarcodedProduct barcodedProduct;
	AddItemViaBarcodeScan testBarcodeItemAdder;
	Order testOrder;
	BaggingAreaListener testBaggingAreaListener;

	@Before
	public void setUp() throws OverloadedDevice {
		// Start a new session
		SelfCheckoutStationSoftware.setStationActive(true);
		
		// Make a power grid for hardware to connect to
		grid = PowerGrid.instance();

		// Initializing hardware objects
		scanner = new BarcodeScanner();
		baggingArea = new ElectronicScale();

		// Power up and enable hardware objects
		scanner.plugIn(grid);
		scanner.turnOn();
		scanner.enable();
		baggingArea.plugIn(grid);
		baggingArea.turnOn();
		baggingArea.enable();

		// Initializing mock barcoded item
		Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
		Barcode barcode = new Barcode(barcodeDigits);
		Mass itemMass = new Mass(1000000000); // 1kg in micrograms
		barcodedItem = new BarcodedItem(barcode, itemMass);

		// Initializing mock product (using same barcode as the barcoded item)
		String productDescription = "test product";
		long productPrice = 5;
		double productWeightInGrams = 1000;
		barcodedProduct = new BarcodedProduct(barcode, productDescription, productPrice, productWeightInGrams);

		// Adding mock product into product database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);

		// Initializing testOrder
		testOrder = new Order(baggingArea);

		// Initializing weightDiscrepancy
		weightDiscrepancy = new WeightDiscrepancy(testOrder, baggingArea);

		// Initializing testBarcodeItemAdder and making it listen to the scanner object
		testBarcodeItemAdder = new AddItemViaBarcodeScan(testOrder);
		scanner.register(testBarcodeItemAdder);

		// Initializing testBaggingAreaListener and making it listen to the scale object
		testBaggingAreaListener = new BaggingAreaListener(testOrder);
		baggingArea.register(testBaggingAreaListener);
	}

	@Test
	public void testProductLookupInDatabase(){
		BarcodedProduct foundProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcodedItem.getBarcode());
		assertNotNull("Product should be found in the database", foundProduct);
		assertEquals("Found product should match the test product that we created", barcodedProduct, foundProduct);


	}

	@Test
	public void testProductLookupInDatabaseWhenNull() {
		Numeral[] nonExistentBarcodeDigits = {Numeral.seven, Numeral.seven, Numeral.seven, Numeral.seven, Numeral.seven};
		Barcode nonExistentBarcode = new Barcode(nonExistentBarcodeDigits);
		BarcodedProduct nullProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(nonExistentBarcode);
		assertNull(nullProduct);


	}

	@Test
	public void testUpdatesTheOrderTotalForPrice(){
		testOrder.addTotalPrice(barcodedProduct.getPrice());
		assertEquals("Order total price should be updated", barcodedProduct.getPrice(),testOrder.getTotalPrice());

	}

	@Test
	public void testUpdatesTheOrderTotalForWeight() {
		double before = testOrder.getTotalWeightInGrams();
		double productWeight = barcodedProduct.getExpectedWeight();
		testOrder.addTotalWeightInGrams(productWeight);

		double after = testOrder.getTotalWeightInGrams();
		assertEquals("Order total for weight should be updated", after, before + productWeight, 0.01);
	}
	@Test
	public void testWeightDiscrepancyOverflow() {
		SelfCheckoutStationSoftware.setStationBlock(false);
		scanner.scan(barcodedItem);

		assertTrue(SelfCheckoutStationSoftware.getStationBlock());
	}

	@Test
	public void testABarcodeHasBeenScannedWhenBlocked() {
		SelfCheckoutStationSoftware.setStationBlock(true);
		scanner.scan(barcodedItem);

		// Item should NOT be added to the order
		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(order.isEmpty());
	}
	
	@Test
	public void testABarcodeHasBeenScannedWhenNoSession() {
		SelfCheckoutStationSoftware.setStationActive(false);
		
		scanner.scan(barcodedItem);

		// Item should NOT be added to the order
		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(order.isEmpty());
	}

	@Test
	public void testABarcodeHasBeenScannedAndAddItemToBaggingArea() throws OverloadedDevice {
		scanner.scan(barcodedItem);
		baggingArea.addAnItem(barcodedItem);

		// System should not be blocked
		assertFalse(SelfCheckoutStationSoftware.getStationBlock());
	}
	@Test
	public void testProductAddNullBarcodeToOrder() {
		Numeral[] nonExistentBarcodeDigits = {Numeral.seven, Numeral.seven, Numeral.seven, Numeral.seven, Numeral.seven};
		Barcode nonExistentBarcode = new Barcode(nonExistentBarcodeDigits);
		Mass fakeMass = new Mass(1000000000); 
		barcodedItem = new BarcodedItem(nonExistentBarcode, fakeMass);
		scanner.scan(barcodedItem);

		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(order.isEmpty());

	}

	@Test
	public void testAddItemToOrder() {
		testOrder.addItemToOrder(barcodedItem);

		// Item should be added to the order
		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(!order.isEmpty());
	}

	@Test
	public void testGetOrderWhenEmpty() {
		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(order.isEmpty());
	}

	@Test
	public void testGetOrderWhenNotEmpty() {
		testOrder.addItemToOrder(barcodedItem);

		ArrayList<Item> order = testOrder.getOrder();
		assertTrue(order.contains(barcodedItem));
	}
	@Test
	public void testAddTotalWeightInGrams() {
		double initialWeight = testOrder.getTotalWeightInGrams(); 
		double weightToAdd = 100.0;
		testOrder.addTotalWeightInGrams(weightToAdd);
		assertEquals("The total weight should be updated correctly", initialWeight + weightToAdd, testOrder.getTotalWeightInGrams(), 0.001);
	}

	@Test
	public void testAddNegativeWeight() {
		double initialWeight = testOrder.getTotalWeightInGrams();
		double weightToAdd = -50.0;
		testOrder.addTotalWeightInGrams(weightToAdd);
		assertEquals("The total weight should decrease when adding negative weight", initialWeight + weightToAdd, testOrder.getTotalWeightInGrams(), 0.001);
	}

	@Test
	public void testAddZeroWeight() {
		double initialWeight = testOrder.getTotalWeightInGrams();
		testOrder.addTotalWeightInGrams(0);
		assertEquals("The total weight should remain unchanged when adding zero", initialWeight, testOrder.getTotalWeightInGrams(), 0.001);
	}

	@Test
	public void testAddTotalPrice() {
		long initialPrice = 100L; // Assuming the constructor or another method sets it to 100
		testOrder.addTotalPrice(initialPrice);

		// Add a specific price to the total
		long priceToAdd = 50L;
		testOrder.addTotalPrice(priceToAdd);

		//Check if the total price has been updated correctly
		long expectedTotalPrice = initialPrice + priceToAdd;
		assertEquals(expectedTotalPrice, testOrder.getTotalPrice()); //method to get the total price
	}

	@Test
	public void testWeightHasChanged() throws OverloadedDevice {
		// test for signals to the system that the weight changed
		// A customer scans an item. 
		scanner.scan(barcodedItem);

		// get the initial mass from the bagging area
		Mass initial = baggingArea.getCurrentMassOnTheScale();
		// A customer places the item in the bagging area
		baggingArea.addAnItem(barcodedItem);
		// get the new mass from the bagging area
		Mass current = baggingArea.getCurrentMassOnTheScale();

		// Initial and current mass should not be the same
		assertNotSame(initial, current);
	}

	@After
	public void tearDown() {
		// de-register listeners 
		scanner.deregister(testBarcodeItemAdder);
		baggingArea.deregister(testBaggingAreaListener);

		// Disable, turn off and un-plug hardware objects
		scanner.disable();
		scanner.turnOff();
		scanner.unplug();
		baggingArea.disable();
		baggingArea.turnOff();
		baggingArea.unplug();

		// Unblock the system
		SelfCheckoutStationSoftware.setStationBlock(false);
	}
}
