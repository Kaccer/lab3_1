package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    ProductData productData;
    ProductType productType;
    TaxPolicy taxPolicy;

    @Before
    public void Initialize() {
        productData = Mockito.mock(ProductData.class);
        productType = ProductType.STANDARD;
        taxPolicy = Mockito.mock(TaxPolicy.class);
    }
    @Test
    public void shouldReturnInvoiceWithOneEntryForSingleElement()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        RequestItem requestItem = new RequestItem(productData, 0, money);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItem);

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        List<InvoiceLine> items = invoice.getItems();


        assertThat(invoice, notNullValue());
        assertThat(items, notNullValue());
        assertThat(items.size(), is(1));

    }

    @Test
    public void shouldCalculateTaxTwiceForInvoiceWithTwoEntries()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        RequestItem requestItem = new RequestItem(productData, 0, money);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy,times(2)).calculateTax(productType,money);
    }

    @Test
    public void shouldNotCalculateTaxForInvoiceWithNoEntries()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy,times(0)).calculateTax(productType,money);
    }

    @Test
    public void shouldCalculateTax500TimesForInvoiceWith500Entries()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        RequestItem requestItem = new RequestItem(productData, 0, money);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        for (int i = 0; i < 500; i++){
            invoiceRequest.add(requestItem);
        }
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy,times(500)).calculateTax(productType,money);
    }

    @Test
    public void shouldReturnInvoiceWith100EntriesFor100Elements()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        RequestItem requestItem = new RequestItem(productData, 0, money);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        for (int i = 0; i < 100; i++){
            invoiceRequest.add(requestItem);
        }
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        List<InvoiceLine> items = invoice.getItems();


        assertThat(invoice, notNullValue());
        assertThat(items, notNullValue());
        assertThat(items.size(), is(100));

    }
    @Test
    public void shouldReturnInvoiceWithNoEntriesFor0Elements()
    {
        when(productData.getType()).thenAnswer(invocationOnMock -> productType);

        Money money = new Money(0.0);
        ClientData clientData = new ClientData(new Id("1"), "product");

        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        Tax tax = new Tax(money, "tax");
        when(taxPolicy.calculateTax(productType, money)).thenAnswer(invocationOnMock -> tax);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        List<InvoiceLine> items = invoice.getItems();


        assertThat(invoice, notNullValue());
        assertThat(items, notNullValue());
        assertThat(items.size(), is(0));

    }

}