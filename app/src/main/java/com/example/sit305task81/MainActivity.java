package com.example.sit305task81;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sit305task81.data.AppDatabase;
import com.example.sit305task81.data.MessageEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String PREFS = "chat_prefs";
    private static final String KEY_USERNAME = "username";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ChatBotClient botClient = new ChatBotClient();

    private AppDatabase database;
    private String username;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(this);
        showLoginScreen();
    }

    private void showLoginScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), 0, dp(28), 0);
        root.setBackground(makeBackground());

        TextView title = new TextView(this);
        title.setText("Welcome,\nLets Chat!");
        title.setTextColor(Color.BLACK);
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.START);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        EditText nameInput = new EditText(this);
        nameInput.setHint("Username");
        nameInput.setSingleLine(true);
        nameInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameInput.setTextSize(14);
        nameInput.setPadding(dp(10), 0, dp(10), 0);
        GradientDrawable inputBackground = rounded(Color.WHITE, dp(4));
        inputBackground.setStroke(dp(1), Color.rgb(85, 85, 85));
        nameInput.setBackground(inputBackground);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
        );
        inputParams.setMargins(0, dp(54), 0, dp(18));
        root.addView(nameInput, inputParams);

        Button goButton = new Button(this);
        goButton.setText("Go");
        goButton.setTextColor(Color.BLACK);
        goButton.setTextSize(14);
        goButton.setTypeface(Typeface.DEFAULT_BOLD);
        goButton.setAllCaps(false);
        goButton.setBackground(rounded(Color.rgb(0, 255, 92), dp(4)));
        root.addView(goButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(46)
        ));

        goButton.setOnClickListener(view -> login(nameInput.getText().toString()));
        nameInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login(nameInput.getText().toString());
                return true;
            }
            return false;
        });

        setContentView(root);
    }

    private void login(String rawName) {
        String cleanName = rawName.trim();
        if (cleanName.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        username = cleanName;
        showChatScreen();
    }

    private void showChatScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(18), dp(14), dp(14));
        root.setBackground(makeBackground());

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.RIGHT);

        Button logoutButton = new Button(this);
        logoutButton.setText("Logout");
        logoutButton.setTextColor(Color.BLACK);
        logoutButton.setTextSize(13);
        logoutButton.setTypeface(Typeface.DEFAULT_BOLD);
        logoutButton.setAllCaps(false);
        logoutButton.setBackground(rounded(Color.rgb(0, 255, 92), dp(4)));
        logoutButton.setOnClickListener(view -> {
            username = "";
            showLoginScreen();
        });

        LinearLayout.LayoutParams logoutParams = new LinearLayout.LayoutParams(dp(96), dp(42));
        topRow.addView(logoutButton, logoutParams);
        LinearLayout.LayoutParams topRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        topRowParams.setMargins(0, dp(18), 0, 0);
        root.addView(topRow, topRowParams);

        chatAdapter = new ChatAdapter();
        chatRecyclerView = new RecyclerView(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setClipToPadding(false);
        chatRecyclerView.setPadding(0, dp(78), 0, dp(10));
        chatRecyclerView.setBackgroundColor(Color.TRANSPARENT);
        root.addView(chatRecyclerView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);

        EditText messageInput = new EditText(this);
        messageInput.setHint("");
        messageInput.setSingleLine(true);
        messageInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageInput.setTextSize(15);
        messageInput.setPadding(dp(10), 0, dp(10), 0);
        messageInput.setBackground(rounded(Color.rgb(226, 226, 226), dp(5)));
        inputRow.addView(messageInput, new LinearLayout.LayoutParams(
                0,
                dp(48),
                1
        ));

        Button sendButton = new Button(this);
        sendButton.setText(">");
        sendButton.setTextSize(18);
        sendButton.setTextColor(Color.WHITE);
        sendButton.setTypeface(Typeface.DEFAULT_BOLD);
        sendButton.setAllCaps(false);
        sendButton.setBackground(rounded(Color.rgb(128, 128, 128), dp(4)));
        LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(dp(44), dp(44));
        sendParams.setMargins(dp(8), 0, 0, 0);
        inputRow.addView(sendButton, sendParams);
        root.addView(inputRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Runnable sendAction = () -> sendMessage(messageInput);
        sendButton.setOnClickListener(view -> sendAction.run());
        messageInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendAction.run();
                return true;
            }
            return false;
        });

        setContentView(root);
        loadMessages();
    }

    private void sendMessage(EditText input) {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }
        input.setText("");

        executor.execute(() -> {
            database.messageDao().insert(new MessageEntity(username, text, true, System.currentTimeMillis()));
            mainHandler.post(this::loadMessages);

            String reply = botClient.ask(username, text);
            database.messageDao().insert(new MessageEntity(username, reply, false, System.currentTimeMillis()));
            mainHandler.post(this::loadMessages);
        });
    }

    private void loadMessages() {
        executor.execute(() -> {
            List<MessageEntity> messages = database.messageDao().messagesForUser(username);
            if (messages.isEmpty()) {
                database.messageDao().insert(new MessageEntity(username, "Welcome " + username + "!", false, System.currentTimeMillis()));
                messages = database.messageDao().messagesForUser(username);
            }
            List<MessageEntity> finalMessages = messages;
            mainHandler.post(() -> renderMessages(finalMessages));
        });
    }

    private void renderMessages(List<MessageEntity> messages) {
        chatAdapter.submitMessages(messages);
        if (!messages.isEmpty()) {
            chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(messages.size() - 1));
        }
    }

    private GradientDrawable makeBackground() {
        return new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(38, 211, 239), Color.rgb(20, 139, 239)}
        );
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
