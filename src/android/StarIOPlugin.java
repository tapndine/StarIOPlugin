package fr.sellsy.cordova;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;


import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExt.Emulation;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.ICommandBuilder.CutPaperAction;
import com.starmicronics.starioextension.ICommandBuilder.AlignmentPosition;
import com.starmicronics.starioextension.ICommandBuilder.CodePageType;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.Layout;


/**
 * This class echoes a string called from JavaScript.
 */
public class StarIOPlugin extends CordovaPlugin {


    private CallbackContext _callbackContext = null;
    String strInterface;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if(_callbackContext == null){
            _callbackContext = callbackContext;
        }

        if (action.equals("checkStatus")) {
            String portName = args.getString(0);
            String portSettings = getPortSettingsOption(portName);
            this.checkStatus(portName, portSettings, callbackContext);
            return true;
        }else if (action.equals("portDiscovery")) {
            String port = args.getString(0);
            this.portDiscovery(port, callbackContext);
            return true;
        }else {
            String portName = args.getString(0);
            String portSettings = getPortSettingsOption(portName);
            String receipt = args.getString(1);

            this.printReceipt(portName, portSettings, receipt, callbackContext);
            return true;
        }
    }


    public void checkStatus(String portName, String portSettings, CallbackContext callbackContext) {

        final Context context = this.cordova.getActivity();
        final CallbackContext _callbackContext = callbackContext;

        final String _portName = portName;
        final String _portSettings = portSettings;

        cordova.getThreadPool()
                .execute(new Runnable() {
                    public void run() {

                        StarIOPort port = null;
                        try {

                            port = StarIOPort.getPort(_portName, _portSettings, 10000, context);

                            // A sleep is used to get time for the socket to completely open
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }

                            StarPrinterStatus status;
                            status = port.retreiveStatus();

                            JSONObject json = new JSONObject();
                            try {
                                json.put("offline", status.offline);
                                json.put("coverOpen", status.coverOpen);
                                json.put("cutterError", status.cutterError);
                                json.put("receiptPaperEmpty", status.receiptPaperEmpty);
                            } catch (JSONException ex) {

                            } finally {
                                _callbackContext.success(json);
                            }


                        } catch (StarIOPortException e) {
                            _callbackContext.error("Failed to connect to printer :" + e.getMessage());
                        } finally {

                            if (port != null) {
                                try {
                                    StarIOPort.releasePort(port);
                                } catch (StarIOPortException e) {
                                    _callbackContext.error("Failed to connect to printer" + e.getMessage());
                                }
                            }

                        }


                    }
                });
    }


    private void portDiscovery(String strInterface, CallbackContext callbackContext) {

        JSONArray result = new JSONArray();
        try {

            if (strInterface.equals("LAN")) {
                result = getPortDiscovery("LAN");
            } else if (strInterface.equals("Bluetooth")) {
                result = getPortDiscovery("Bluetooth");
            } else if (strInterface.equals("USB")) {
                result = getPortDiscovery("USB");
            } else {
                result = getPortDiscovery("All");
            }

        } catch (StarIOPortException exception) {
            callbackContext.error(exception.getMessage());

        } catch (JSONException e) {

        } finally {

            Log.d("Discovered ports", result.toString());
            callbackContext.success(result);
        }
    }


    private JSONArray getPortDiscovery(String interfaceName) throws StarIOPortException, JSONException {
        List<PortInfo> BTPortList;
        List<PortInfo> TCPPortList;
        List<PortInfo> USBPortList;

        final Context context = this.cordova.getActivity();
        final ArrayList<PortInfo> arrayDiscovery = new ArrayList<PortInfo>();

        JSONArray arrayPorts = new JSONArray();


        if (interfaceName.equals("Bluetooth") || interfaceName.equals("All")) {
            BTPortList = StarIOPort.searchPrinter("BT:");

            for (PortInfo portInfo : BTPortList) {
                arrayDiscovery.add(portInfo);
            }
        }
        if (interfaceName.equals("LAN") || interfaceName.equals("All")) {
            TCPPortList = StarIOPort.searchPrinter("TCP:");
            for (PortInfo portInfo : TCPPortList) {
                arrayDiscovery.add(portInfo);
            }
        }
        if (interfaceName.equals("USB") || interfaceName.equals("All")) {
            USBPortList = StarIOPort.searchPrinter("USB:", context);

            for (PortInfo portInfo : USBPortList) {
                arrayDiscovery.add(portInfo);
            }
        }

        for (PortInfo discovery : arrayDiscovery) {
            String portName;

            JSONObject port = new JSONObject();
            port.put("name", discovery.getPortName());

            if (!discovery.getMacAddress().equals("")) {

                port.put("macAddress", discovery.getMacAddress());

                if (!discovery.getModelName().equals("")) {
                    port.put("modelName", discovery.getModelName());
                }
            } else if (interfaceName.equals("USB") || interfaceName.equals("All")) {
                if (!discovery.getModelName().equals("")) {
                    port.put("modelName", discovery.getModelName());
                }
                if (!discovery.getUSBSerialNumber().equals(" SN:")) {
                    port.put("USBSerialNumber", discovery.getUSBSerialNumber());
                }
            }

            arrayPorts.put(port);
        }

        return arrayPorts;
    }




    private String getPortSettingsOption(String portName) {
        String portSettings = "";

        if (portName.toUpperCase(Locale.US).startsWith("TCP:")) {
            portSettings += ""; // retry to yes
        } else if (portName.toUpperCase(Locale.US).startsWith("BT:")) {
            portSettings += ";p"; // or ";p"
            portSettings += ";l"; // standard
        }

        return portSettings;
    }

    public byte[] createReceipt(Emulation emulation, String receiptText) {
        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);
        String textToPrint = receiptText;

        int textSize = 25;
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);

        Paint paint = new Paint();
        Bitmap bitmap;
        Canvas canvas;

        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        paint.getTextBounds(textToPrint, 0, textToPrint.length(), new Rect());

        TextPaint textPaint = new TextPaint(paint);
        // 576 for width
        StaticLayout staticLayout = new StaticLayout(textToPrint, textPaint, 576, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        // Create bitmap
        bitmap = Bitmap.createBitmap(staticLayout.getWidth(), staticLayout.getHeight(), Bitmap.Config.ARGB_8888);

        // Create canvas
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.translate(0, 0);
        staticLayout.draw(canvas);

        builder.beginDocument();
        builder.appendBitmap(bitmap, false);
        builder.appendCutPaper(CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        return builder.getCommands();
    }


    private boolean printReceipt(String portName, String portSettings, String receipt, CallbackContext callbackContext) throws JSONException {

        Context context = this.cordova.getActivity();
        return sendCommand(context, portName, portSettings, receipt, callbackContext);
    }

    private boolean sendCommand(Context context, String portName, String portSettings, String receipt, CallbackContext callbackContext) {
        StarIOPort port = null;
        try {
			/*
			 * using StarIOPort3.1.jar (support USB Port) Android OS Version: upper 2.2
			 */
            port = StarIOPort.getPort(portName, portSettings, 30000, context);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

			/*
			 * Using Begin / End Checked Block method When sending large amounts of raster data,
			 * adjust the value in the timeout in the "StarIOPort.getPort" in order to prevent
			 * "timeout" of the "endCheckedBlock method" while a printing.
			 *
			 * If receipt print is success but timeout error occurs(Show message which is "There
			 * was no response of the printer within the timeout period." ), need to change value
			 * of timeout more longer in "StarIOPort.getPort" method.
			 * (e.g.) 10000 -> 30000
			 */
            StarPrinterStatus status = port.beginCheckedBlock();

            if (true == status.offline) {
                //throw new StarIOPortException("A printer is offline");
                sendEvent("printerOffline", null);
                return false;
            }
            Emulation em = Emulation.StarGraphic;
            byte[] commandToSendToPrinter = createReceipt(em, receipt);
            port.writePort(commandToSendToPrinter, 0, commandToSendToPrinter.length);

            port.setEndCheckedBlockTimeoutMillis(30000);// Change the timeout time of endCheckedBlock method.
            status = port.endCheckedBlock();

            if (status.coverOpen == true) {
                callbackContext.error("Cover open");
                sendEvent("printerCoverOpen", null);
                return false;
            } if (status.receiptPaperEmpty == true) {
                callbackContext.error("Empty paper");
                sendEvent("printerPaperEmpty", null);
                return false;
            } if (status.offline == true) {
                callbackContext.error("Printer offline");
                sendEvent("printerOffline", null);
                return false;
            }
            callbackContext.success("Printed");

        } catch (StarIOPortException e) {
            sendEvent("printerImpossible", e.getMessage());
            callbackContext.error(e.getMessage());
        } finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort(port);
                } catch (StarIOPortException e) {
                }
            }
            return true;
        }
    }


    private byte[] createCpUTF8(String inputText) {
        byte[] byteBuffer = null;

        try {
            byteBuffer = inputText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            byteBuffer = inputText.getBytes();
        }

        return byteBuffer;
    }


    private byte[] convertFromListByteArrayTobyteArray(List<byte[]> ByteArray) {
        int dataLength = 0;
        for (int i = 0; i < ByteArray.size(); i++) {
            dataLength += ByteArray.get(i).length;
        }

        int distPosition = 0;
        byte[] byteArray = new byte[dataLength];
        for (int i = 0; i < ByteArray.size(); i++) {
            System.arraycopy(ByteArray.get(i), 0, byteArray, distPosition, ByteArray.get(i).length);
            distPosition += ByteArray.get(i).length;
        }

        return byteArray;
    }

    /**
     * Create a new plugin result and send it back to JavaScript
     *
     * @param dataType event type
     */
    private void sendEvent(String dataType, String info) {
        if (this._callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(true);
            this._callbackContext.sendPluginResult(result);
        }
    }


}



