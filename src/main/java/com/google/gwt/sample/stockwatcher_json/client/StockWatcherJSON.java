package com.google.gwt.sample.stockwatcher_json.client;

import static com.google.gwt.sample.stockwatcher_json.url.URLConstants.JSON_RANDOMIZE;
import static com.google.gwt.sample.stockwatcher_json.url.URLConstants.JSON_STOCK_PRICES;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcherJSON implements EntryPoint
{
	private static final int REFRESH_INTERVAL = 5000; // ms
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Button randomizeButton = new Button("Randomize");
	private Label lastUpdatedLabel = new Label();
	private Label randomizeLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<String>();

	private Label errorMsgLabel = new Label();
	private static final String JSON_STOCK_PRICES_URL = GWT.getModuleBaseURL() + JSON_STOCK_PRICES + "?q=";
	private static final String JSON_RANDOMIZE_URL = GWT.getModuleBaseURL() + JSON_RANDOMIZE;

	/**
	 * Entry point method.
	 */
	public void onModuleLoad()
	{

		stocksFlexTable.setText(0, 0, "Symbol");
		stocksFlexTable.setText(0, 1, "Price");
		stocksFlexTable.setText(0, 2, "Change");
		stocksFlexTable.setText(0, 3, "Remove");
		stocksFlexTable.setCellPadding(6);

		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getColumnFormatter().addStyleName(1, "watchListNumericColumn");
		stocksFlexTable.getColumnFormatter().addStyleName(2, "watchListNumericColumn");
		stocksFlexTable.getColumnFormatter().addStyleName(3, "watchListRemoveColumn");

		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.add(randomizeButton);
		addPanel.setStyleName("addPanel");

		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);

		randomizeLabel.setVisible(false);
		mainPanel.add(errorMsgLabel);
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);
		mainPanel.add(new HTML("<br/>"));
		mainPanel.add(randomizeLabel);

		RootPanel.get("stockList").add(mainPanel);

		newSymbolTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				addStock();
			}
		});

		randomizeButton.addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				randomizeNumber();

			}

		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
				{
					addStock();
				}
			}
		});

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer()
		{
			@Override
			public void run()
			{
				if (!stocks.isEmpty())
				{
					refreshWatchList();
				}
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}

	private void randomizeNumber()
	{
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, JSON_RANDOMIZE);

		try
		{
			builder.sendRequest(null, new RequestCallback()
			{
				public void onError(Request request, Throwable exception)
				{
					displayError("Couldn't retrieve JSON : " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response)
				{
					if (200 == response.getStatusCode())
					{
						StockWatcherJSON.this.randomizeLabel.setText(response.getText());
						StockWatcherJSON.this.randomizeLabel.setVisible(true);
					}
					else
					{
						displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
						StockWatcherJSON.this.randomizeLabel.setVisible(false);
					}
				}
			});
		}
		catch (RequestException e)
		{
			displayError("Couldn't retrieve JSON : " + e.getMessage());
			StockWatcherJSON.this.randomizeLabel.setVisible(false);
		}
	}

	private void addStock()
	{
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		newSymbolTextBox.setFocus(true);

		if (!symbol.matches("^[0-9A-Z\\.]{1,10}$"))
		{
			Window.alert("'" + symbol + "' is not a valid symbol.");
			newSymbolTextBox.selectAll();
			return;
		}

		newSymbolTextBox.setText("");

		if (stocks.contains(symbol))
		{
			Window.alert("This stock already exists");
		}
		else
		{
			int row = stocksFlexTable.getRowCount();
			stocks.add(symbol);
			stocksFlexTable.setText(row, 0, symbol);
			stocksFlexTable.setWidget(row, 2, new Label());

			Button removeRowButton = new Button("X");
			removeRowButton.addStyleDependentName("remove");
			removeRowButton.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					int rowIndex = stocks.indexOf(symbol);
					stocks.remove(symbol);
					stocksFlexTable.removeRow(rowIndex + 1);
				}
			});

			stocksFlexTable.setWidget(row, 3, removeRowButton);
		}

		refreshWatchList();

	}

	private void refreshWatchList()
	{
		if (stocks.size() == 0)
		{
			return;
		}

		String url = JSON_STOCK_PRICES_URL;

		// Append watch list stock symbols to query URL.
		Iterator<String> iter = stocks.iterator();
		while (iter.hasNext())
		{
			url += iter.next();
			if (iter.hasNext())
			{
				url += "+";
			}
		}

		url = URL.encode(url);
		GWT.log("url = " + url);
		// Send request to server and catch any errors.
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try
		{
			builder.sendRequest(null, new RequestCallback()
			{
				public void onError(Request request, Throwable exception)
				{
					displayError("Couldn't retrieve JSON : " + exception.getMessage());
				}

				public void onResponseReceived(Request request, Response response)
				{
					if (200 == response.getStatusCode())
					{
						updateTable(asArrayOfStockData(response.getText()));
					}
					else
					{
						displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
					}
				}
			});
		}
		catch (RequestException e)
		{
			displayError("Couldn't retrieve JSON : " + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private void updateTable(JsArray<StockData> prices)
	{
		for (int i = 0; i < prices.length(); i++)
		{
			updateTable(prices.get(i));
		}

		// Display timestamp showing last refresh.
		lastUpdatedLabel.setText("Last update : " + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

		// Clear any errors.
		errorMsgLabel.setVisible(false);
	}

	private void updateTable(StockData price)
	{
		// Make sure the stock is still in the stock table.
		if (!stocks.contains(price.getSymbol()))
		{
			return;
		}

		int row = stocks.indexOf(price.getSymbol()) + 1;

		// Format the data in the Price and Change fields.
		String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(price.getChange());
		String changePercentText = changeFormat.format(price.getChangePercent());

		// Populate the Price and Change fields with new data.
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + " (" + changePercentText + "%)");

		// Change the color of text in the Change field based on its value.
		String changeStyleName = "noChange";
		if (price.getChangePercent() < -0.1f)
		{
			changeStyleName = "negativeChange";
		}
		else if (price.getChangePercent() > 0.1f)
		{
			changeStyleName = "positiveChange";
		}

		changeWidget.setStyleName(changeStyleName);
	}

	private final native JsArray<StockData> asArrayOfStockData(String json)
	/*-{
		return eval(json);
	}-*/;

	/**
	 * If can't get JSON, display error message.
	 * 
	 * @param error
	 */
	private void displayError(String error)
	{
		errorMsgLabel.setText("Error: " + error);
		errorMsgLabel.setVisible(true);
	}
}
