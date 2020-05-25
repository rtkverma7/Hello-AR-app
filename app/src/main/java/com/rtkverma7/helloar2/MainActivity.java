/*
 *  Hello AR app using Sceneform SDK and ARCore.
 *  Author : Ritik Verma ( github : rtkverma7 )
 *  Reference : https://www.youtube.com/playlist?list=PLsOU6EOcj51cEDYpCLK_bzo4qtjOwDWfW
 *
 *  This app displays a 3D Model (Arctic Fox) on a detected surface using ARCore Sceneform SDK
 *
 */

package com.rtkverma7.helloar2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/*
 *  All the key points have been added as comments. Please go through the following files in order:-
 *  build.gradle (app) >> AndroidManifest.xml >> activity_main.xml >> MainActivity.java >> build.gradle (project)
 *
 *  Please install the Google Sceneform Tools plugin from preferences.
 *
 */
public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        /*
            This method will be executed when a user taps on the plane detected by the arFragment.
            We are using a lambda expression for simplicity
         */
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            /*
                Anchor is used to describe a fixed location and orientation in the real world.
                We will render our 3D model on top of this anchor.
             */
            Anchor anchor = hitResult.createAnchor();

            // To build our model using the sceneform asset (*.sfb)
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                    .exceptionally(throwable -> {

                        // To show message if error occurs during building the model.
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();

                        return null;
                    });
        });
    }

    /*
        This method adds the accepted model to the AR scene
     */
    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        // anchorNode positions itself in the world space based on the ARCore anchor we have passed
        AnchorNode anchorNode = new AnchorNode(anchor);

        // anchorNode's position cannot be changed or it cannot be zoomed in.
        // So we use TransformableNode over the AnchorNode.
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);

        // Assigning the rendered model to the T. node
        transformableNode.setRenderable(modelRenderable);

        // Adding the model to the scene. anchorNode ( the parent ) is added else model won't render.
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
}
