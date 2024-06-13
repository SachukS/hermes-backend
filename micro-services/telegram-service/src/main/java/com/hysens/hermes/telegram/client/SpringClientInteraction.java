package com.hysens.hermes.telegram.client;

import it.tdlight.client.*;
import it.tdlight.common.utils.ScannerUtils;
import it.tdlight.jni.TdApi;



import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.hysens.hermes.telegram.client.Telegram.QRCodeFrame;

public class SpringClientInteraction implements ClientInteraction {
    private final ExecutorService blockingExecutor;
    private final Authenticable authenticable;

    public static String qr = "";

    public SpringClientInteraction(ExecutorService blockingExecutor, Authenticable authenticable) {
        this.blockingExecutor = blockingExecutor;
        this.authenticable = authenticable;
    }

    @Override
    public void onParameterRequest(InputParameter parameter, ParameterInfo parameterInfo, Consumer<String> result) {
        authenticable.getAuthenticationData(authenticationData -> {
            blockingExecutor.execute(() -> {
                String who;
                boolean useRealWho;
                if (authenticationData instanceof ConsoleInteractiveAuthenticationData) {
                    useRealWho = ((ConsoleInteractiveAuthenticationData) authenticationData).isInitialized();
                } else {
                    useRealWho = true;
                }
                if (!useRealWho) {
                    who = "login";
                } else if (authenticationData.isQrCode()) {
                    who = "QR login";
                } else if (authenticationData.isBot()) {
                    who = authenticationData.getBotToken().split(":", 2)[0];
                } else {
                    who = "+" + authenticationData.getUserPhoneNumber();
                }
                String question;
                boolean trim = false;
                switch (parameter) {
                    case ASK_FIRST_NAME:
                        question = "Enter first name";
                        trim = true;
                        break;
                    case ASK_LAST_NAME:
                        question = "Enter last name";
                        trim = true;
                        break;
                    case ASK_CODE:
                        question = "Enter authentication code";
                        ParameterInfoCode codeInfo = ((ParameterInfoCode) parameterInfo);
                        question += "\n\tPhone number: " + codeInfo.getPhoneNumber();
                        question += "\n\tTimeout: " + codeInfo.getTimeout() + " seconds";
                        question += "\n\tCode type: " + codeInfo.getType().getClass().getSimpleName()
                                .replace("AuthenticationCodeType", "");
                        if (codeInfo.getNextType() != null) {
                            question += "\n\tNext code type: " + codeInfo
                                    .getNextType()
                                    .getClass()
                                    .getSimpleName()
                                    .replace("AuthenticationCodeType", "");
                        }
                        trim = true;
                        break;
                    case ASK_PASSWORD:
                        question = "Enter your password";
                        String passwordMessage = "Password authorization:";
                        String hint = ((ParameterInfoPasswordHint) parameterInfo).getHint();
                        if (hint != null && !hint.isEmpty()) {
                            passwordMessage += "\n\tHint: " + hint;
                        }
                        boolean hasRecoveryEmailAddress = ((ParameterInfoPasswordHint) parameterInfo)
                                .hasRecoveryEmailAddress();
                        passwordMessage += "\n\tHas recovery email: " + hasRecoveryEmailAddress;
                        String recoveryEmailAddressPattern = ((ParameterInfoPasswordHint) parameterInfo)
                                .getRecoveryEmailAddressPattern();
                        if (recoveryEmailAddressPattern != null && !recoveryEmailAddressPattern.isEmpty()) {
                            passwordMessage += "\n\tRecovery email address pattern: " + recoveryEmailAddressPattern;
                        }
                        System.out.println(passwordMessage);
                        break;
                    case NOTIFY_LINK:
                        String link = ((ParameterInfoNotifyLink) parameterInfo).getLink();
                        System.out.println();
                        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        if (QRCodeFrame.isEnabled())
                            QRCodeFrame.dispose();

                        QRCodeFrame.setUndecorated(true);

                        qr = link;

//                        ImageIcon image = new ImageIcon(
//                                QRAuthorize.getQr(link)
//                                        .getScaledInstance(256, 256,  Image.SCALE_SMOOTH));
//
//                        JLabel lbl = new JLabel(image);
//                        QRCodeFrame.getContentPane().add(lbl);
//                        QRCodeFrame.setSize(256, 256);
//
//                        int x = (screenSize.width - QRCodeFrame.getSize().width)/2;
//                        int y = (screenSize.height - QRCodeFrame.getSize().height)/2;
//
//                        QRCodeFrame.setLocation(x, y);
//                        QRCodeFrame.setVisible(true);
                        result.accept("");
                        return;
                    case TERMS_OF_SERVICE:
                        TdApi.TermsOfService tos = ((ParameterInfoTermsOfService) parameterInfo).getTermsOfService();
                        question = "Terms of service:\n\t" + tos.text.text;
                        if (tos.minUserAge > 0) {
                            question += "\n\tMinimum user age: " + tos.minUserAge;
                        }
                        if (tos.showPopup) {
                            question += "\nPlease press enter.";
                            trim = true;
                        } else {
                            System.out.println(question);
                            result.accept("");
                            return;
                        }
                        break;
                    default:
                        question = parameter.toString();
                        break;
                }
                String resultSpring = ScannerUtils.askParameter(who, question);
                if (trim) {
                    result.accept(resultSpring.trim());
                } else {
                    result.accept(resultSpring);
                }
            });
        });
    }
}

