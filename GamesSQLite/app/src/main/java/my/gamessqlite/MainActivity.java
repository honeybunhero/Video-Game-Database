package my.gamessqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    EditText gameTxt, platformTxt;
    ListView dbList;
    Button addBtn, removeBtn, viewBtn, searchBtn, searchWebBtn, tableSwitchBtn;
    boolean showGamesWanted, showGamesOwned;
    String gamesOwnedTable, gamesWantedTable, tableUsed;
    ArrayAdapter gameArrayAdaptor;
    DataBaseHelper dataBaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gamesOwnedTable = "Game_Table";
        gamesWantedTable = "Game_Wanted_Table";

        gameTxt = findViewById(R.id.game);
        platformTxt = findViewById(R.id.console);
        dbList = findViewById(R.id.dbList);

        addBtn = findViewById(R.id.addBtn);
        removeBtn = findViewById(R.id.removeBtn);
        viewBtn = findViewById(R.id.viewBtn);
        searchBtn = findViewById(R.id.searchBtn);
        searchWebBtn = findViewById(R.id.searchWebBtn);
        tableSwitchBtn = findViewById(R.id.tableSwitchBtn);

        showGamesOwned = true;
        showGamesWanted = false;
        tableUsed = gamesOwnedTable;

        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        ShowGamesOnListView(dataBaseHelper);

        tableSwitchBtn.setText("Own");

        tableSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showGamesOwned) {
                    showGamesWanted = true;
                    showGamesOwned = false;
                    tableSwitchBtn.setText("Want");
                    SetTable(gamesWantedTable);
                    ShowGamesOnListView(dataBaseHelper);
                } else if (showGamesWanted) {
                    showGamesOwned = true;
                    showGamesWanted = false;
                    tableSwitchBtn.setText("Own");
                    SetTable(gamesOwnedTable);
                    ShowGamesOnListView(dataBaseHelper);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameInput = gameTxt.getText().toString();
                String platformInput = platformTxt.getText().toString();

                GameInformation gameInformation;

                if (dataBaseHelper.SearchForExactGame(gameInput, platformInput, tableUsed)) {
                    Toast.makeText(MainActivity.this, "You already have this game added!", Toast.LENGTH_SHORT).show();
                    gameTxt.setText("");
                    platformTxt.setText("");
                    return;
                }

                if (!gameInput.equalsIgnoreCase("") && !platformInput.equalsIgnoreCase("")) {
                    gameInformation = new GameInformation(gameInput, PlatformInput(platformInput));
                    gameTxt.setText("");
                    platformTxt.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "You missed some information", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dataBaseHelper.AddOne(gameInformation, tableUsed);

                ShowGamesOnListView(dataBaseHelper);
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameInput = gameTxt.getText().toString();
                String platformInput = platformTxt.getText().toString();

                GameInformation gameInformation = null;
                try {
                    if (!gameInput.equalsIgnoreCase("") && !platformInput.equalsIgnoreCase("")) {
                        gameInformation = new GameInformation(gameInput, PlatformInput(platformInput));
                        gameTxt.setText("");
                        platformTxt.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "You missed some information", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dataBaseHelper.RemoveOne(gameInformation, tableUsed);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something failed " + gameInformation.getName(), Toast.LENGTH_SHORT).show();
                }
                ShowGamesOnListView(dataBaseHelper);
            }
        });


        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper = new DataBaseHelper(MainActivity.this);
                ShowGamesOnListView(dataBaseHelper);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper = new DataBaseHelper(MainActivity.this);
                GameInformation gameInformation = null;
                String platformName = platformTxt.getText().toString();
                String gameName = gameTxt.getText().toString();
                gameInformation = new GameInformation(gameName, platformName);
                try {
                    ShowSearchResultListView(dataBaseHelper, gameInformation);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something went wrong with " + gameName, Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchWebBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameTitle = gameTxt.getText().toString();
                BrowserSearch(v, gameTitle);
            }
        });
        dbList.setAdapter(gameArrayAdaptor);

        dbList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                EasterEgg();
                return false;
            }
        });
        dbList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position).toString();
                String[] nameSplit = name.split(" : ");
                try {
                    gameTxt.setText(nameSplit[0]);
                    platformTxt.setText(nameSplit[1]);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Something messed up", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    String Test(String words) {
        return words;
    }

    private void ShowSearchResultListView(DataBaseHelper dataBaseHelper, GameInformation gameInformation) {
        gameArrayAdaptor = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.Search(gameInformation, tableUsed));
        dbList.setAdapter(gameArrayAdaptor);
    }

    private void ShowGamesOnListView(DataBaseHelper dataBaseHelper) {
        gameArrayAdaptor = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.GetEverything(tableUsed));
        dbList.setAdapter(gameArrayAdaptor);
    }

    String PlatformInput(String input) {
        String selectedPlatform = null;

        if (input.equalsIgnoreCase("PS1") ||
                input.equalsIgnoreCase("Playstation") ||
                input.equalsIgnoreCase("Playstation 1") ||
                input.equalsIgnoreCase("Playstation One")) {
            selectedPlatform = "Playstation";
        } else if (input.equalsIgnoreCase("PS2") ||
                input.equalsIgnoreCase("Playstation 2") ||
                input.equalsIgnoreCase("Playstation Two")) {
            selectedPlatform = "Playstation 2";
        } else if (input.equalsIgnoreCase("PS3") ||
                input.equalsIgnoreCase("Playstation 3") ||
                input.equalsIgnoreCase("Playstation Three")) {
            selectedPlatform = "Playstation 3";
        } else if (input.equalsIgnoreCase("PS4") ||
                input.equalsIgnoreCase("Playstation 4") ||
                input.equalsIgnoreCase("Playstation Four")) {
            selectedPlatform = "Playstation 4";
        } else if (input.equalsIgnoreCase("Xbox")) {
            selectedPlatform = "Xbox";
        } else if (input.equalsIgnoreCase("Xbox360") ||
                input.equalsIgnoreCase("Xbox 360") ||
                input.equalsIgnoreCase("X360")) {
            selectedPlatform = "Xbox 360";
        } else if (input.equalsIgnoreCase("XboxOne") ||
                input.equalsIgnoreCase("Xbox One") ||
                input.equalsIgnoreCase("Xbone")) {
            selectedPlatform = "Xbox One";
        } else if (input.equalsIgnoreCase("GB") ||
                input.equalsIgnoreCase("GameBoy")) {
            selectedPlatform = "Gameboy";
        } else if (input.equalsIgnoreCase("GBC") ||
                input.equalsIgnoreCase("GameboyColor") ||
                input.equalsIgnoreCase("Game Boy Color") ||
                input.equalsIgnoreCase("GameBoy Color")) {
            selectedPlatform = "Gameboy Color";
        } else if (input.equalsIgnoreCase("GBA") ||
                input.equalsIgnoreCase("GameBoyAdvance") ||
                input.equalsIgnoreCase("GameBoy Advance")) {
            selectedPlatform = "Gameboy Advance";
        } else if (input.equalsIgnoreCase("DS") ||
                input.equalsIgnoreCase("Nintendo DS") ||
                input.equalsIgnoreCase("NDS") ||
                input.equalsIgnoreCase("Nintendo Dual Screen") ||
                input.equalsIgnoreCase("NintendoDualScreen")) {
            selectedPlatform = "Nintendo DS";
        } else if (input.equalsIgnoreCase("N3DS") ||
                input.equalsIgnoreCase("Nintendo 3DS") ||
                input.equalsIgnoreCase("Nintendo3DS") ||
                input.equalsIgnoreCase("3DS")) {
            selectedPlatform = "Nintendo 3DS";
        } else if (input.equalsIgnoreCase("N64") ||
                input.equalsIgnoreCase("Nintendo 64") ||
                input.equalsIgnoreCase("Nintendo64")) {
            selectedPlatform = "Nintendo 64";
        } else if (input.equalsIgnoreCase("NGC") ||
                input.equalsIgnoreCase("Nintendo GameCube") ||
                input.equalsIgnoreCase("GameCube")) {
            selectedPlatform = "Nintendo Gamecube";
        } else if (input.equalsIgnoreCase("Dreamcast") ||
                input.equalsIgnoreCase("DC") ||
                input.equalsIgnoreCase("Sega Dreamcast") ||
                input.equalsIgnoreCase("SDC")) {
            selectedPlatform = "Dreamcast";
        } else if (input.equalsIgnoreCase("Gamegear") ||
                input.equalsIgnoreCase("GG") ||
                input.equalsIgnoreCase("Sega GameGear") ||
                input.equalsIgnoreCase("SGG")) {
            selectedPlatform = "Gamegear";
        } else if (input.equalsIgnoreCase("NES") ||
                input.equalsIgnoreCase("Nintendo Entertainment System")) {
            selectedPlatform = "Nintendo Entertainment System";
        } else if (input.equalsIgnoreCase("SNES") ||
                input.equalsIgnoreCase("Super Nintendo Entertainment System")) {
            selectedPlatform = "Super Nintendo Entertainment System";
        } else if (input.equalsIgnoreCase("Wii") ||
                input.equalsIgnoreCase("Nintendo Wii")) {
            selectedPlatform = "Nintendo Wii";
        } else if (input.equalsIgnoreCase("WIIU") ||
                input.equalsIgnoreCase("Nintendo WiiU")) {
            selectedPlatform = "Nintendo WiiU";
        } else if (input.equalsIgnoreCase("Switch") ||
                input.equalsIgnoreCase("Nintendo Switch")) {
            selectedPlatform = "Nintendo Switch";
        } else if (input.equalsIgnoreCase("PSP") ||
                input.equalsIgnoreCase("Playstation Portable")
                || input.equalsIgnoreCase("PlaystationPortable")) {
            selectedPlatform = "PSP";
        } else if (input.equalsIgnoreCase("PSVita") ||
                input.equalsIgnoreCase("PlaystationVita") ||
                input.equalsIgnoreCase("Playstation Vita")) {
            selectedPlatform = "PSVita";
        } else {
            selectedPlatform = "error";
            Toast.makeText(this, "There was an error in the platform you entered", Toast.LENGTH_SHORT).show();
            return selectedPlatform;
        }

        return selectedPlatform;
    }

    String SetTable(String tableToUse) {
        tableUsed = tableToUse;
        return tableUsed;
    }

    void BrowserSearch(View view, String title) {
        if (title.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter a game title", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + title + " achievement+guide+and+roadmap"));
        startActivity(browserIntent);
    }

    void EasterEgg() {
        Random r = new Random();
        int randomNum = r.nextInt(4);
        switch (randomNum) {
            case 0:
                Toast.makeText(this, "Don't Touch Me!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "Stooooooop!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Meep.", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "Did you find me?", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}