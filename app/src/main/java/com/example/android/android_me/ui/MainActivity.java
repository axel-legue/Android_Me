/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.android_me.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

// This activity is responsible for displaying the master list of all images
// Implement the MasterListFragment callback, OnImageClickListener
public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener {

    // Variables to store the values for the list index of the selected images
    // The default value will be index = 0
    private int headIndex;
    private int bodyIndex;
    private int legIndex;

    private boolean mtwoPane;

    // TODO (3) Create a variable to track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Détermine si on est en train de créer une vue avec 2 panneaux ou un seul panneau
        if (findViewById(R.id.android_me_linear_layout) != null) {
            // Ce linearLayout existe uniquement dans l'activité avec 2 panneaux
            mtwoPane = true;

            // Change le grid view pour espacer les images sur la tablette
            GridView gridView = (GridView) findViewById(R.id.images_grid_view);
            gridView.setNumColumns(2);

            // On cache la vue du boutton Next sur la tablette
            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setVisibility(View.GONE);



            if (savedInstanceState == null) {
                // En mode 2 panneaux, on ajoute un BodyPartFragments initial à l'écran
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Creation d'un nouveau fragment head
                BodyPartFragment headFragment = new BodyPartFragment();
                headFragment.setImageIds(AndroidImageAssets.getHeads());
                // on ajoute le fragment a son conteneur en utilisant la transaction
                fragmentManager.beginTransaction().add(R.id.head_container, headFragment).commit();

                // Nouveau Body Fragment
                BodyPartFragment bodyFragment = new BodyPartFragment();
                bodyFragment.setImageIds(AndroidImageAssets.getBodies());
                fragmentManager.beginTransaction().add(R.id.body_container, bodyFragment).commit();

                // Nouveau Leg Fragment
                BodyPartFragment legFragment = new BodyPartFragment();
                legFragment.setImageIds(AndroidImageAssets.getLegs());
                fragmentManager.beginTransaction().add(R.id.leg_container, legFragment).commit();
            }
        } else {
            mtwoPane = false;
        }

    }

    // Define the behavior for onImageSelected
    public void onImageSelected(int position) {
        // Create a Toast that displays the position that was clicked
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();

        // bodyPartNumber sera = 0 pour le fragment head, 1 pour le body et 2 pour les legs
        // En divisant par 12 on obtient ses valeurs car il y a 12 images pour chaque type de resources
        int bodyPartNumber = position / 12;

        // Cela permet de s'assurer que l'index sera toujours compris entre 0 et 11
        int listIndex = position - 12 * bodyPartNumber;

        // Gère le cas des deux panneaux et remplace les fragments existants quand une nouvelle image est sélectionnée à partir de la master List
        if (mtwoPane) {
            // Creation de l'intéraction des 2 panneaux
            BodyPartFragment newFragment = new BodyPartFragment();

            // Définir l'élément actuellement affiché  pour la bonne partie du corps
            switch (bodyPartNumber) {
                case 0:
                    // Une image de visage a été cliqué
                    // on donne la bonne ressource image au nouveau fragment
                    newFragment.setImageIds(AndroidImageAssets.getHeads());
                    newFragment.setListIndex(listIndex);
                    // Remplace l'ancien fragment part le nouveau
                    getSupportFragmentManager().beginTransaction().replace(R.id.head_container, newFragment).commit();
                    break;
                case 1:
                    newFragment.setImageIds(AndroidImageAssets.getBodies());
                    newFragment.setListIndex(listIndex);
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container, newFragment).commit();
                    break;
                case 2:
                    newFragment.setImageIds(AndroidImageAssets.getLegs());
                    newFragment.setListIndex(listIndex);
                    getSupportFragmentManager().beginTransaction().replace(R.id.leg_container, newFragment).commit();
                    break;
                default:
                    break;
            }

        } else {
            // Gestion du cas 1 panneau
            switch (bodyPartNumber) {
                case 0:
                    headIndex = listIndex;
                    break;
                case 1:
                    bodyIndex = listIndex;
                    break;
                case 2:
                    legIndex = listIndex;
                    break;
                default:
                    break;
            }
            // Put this information in a Bundle and attach it to an Intent that will launch an AndroidMeActivity
            Bundle b = new Bundle();
            b.putInt("headIndex", headIndex);
            b.putInt("bodyIndex", bodyIndex);
            b.putInt("legIndex", legIndex);

            // Attach the Bundle to an intent
            final Intent intent = new Intent(this, AndroidMeActivity.class);
            intent.putExtras(b);

            // The "Next" button launches a new AndroidMeActivity
            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                }
            });
        }
    }

}
