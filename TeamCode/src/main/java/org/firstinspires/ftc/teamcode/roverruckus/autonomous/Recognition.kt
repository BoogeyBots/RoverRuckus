package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

fun Robot.initVuforia() {
    /*
     * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
     */
    val parameters = VuforiaLocalizer.Parameters()

    parameters.vuforiaLicenseKey = Robot.VUFORIA_KEY
    parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

    //  Instantiate the Vuforia engine
    vuforia = ClassFactory.getInstance().createVuforia(parameters)

    // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
}

/**
 * Initialize the Tensor Flow Object Detection engine.
 */
fun Robot.initTfod() {
    val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
    val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia)
    tfod?.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
}

fun Robot.recognizeGold(): String {
    var k = 1
    val elapsedTime = ElapsedTime()

    if (opModeIsActive) {
        /** Activate Tensor Flow Object Detection.  */
        if (tfod != null) {
            tfod?.activate()
        }

        elapsedTime.reset()

        var foundGold = false
        while (opModeIsActive && !foundGold) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                val updatedRecognitions = tfod?.updatedRecognitions
                if (updatedRecognitions != null) {
                    telemetry.addData("# Objects Detected", updatedRecognitions.size)
                    if (updatedRecognitions.size > 0) {
                        for (recognition in updatedRecognitions) {
                            if (recognition.label == LABEL_GOLD_MINERAL) {
                                foundGold = true
                            } else {
                                if (elapsedTime.seconds() > 0.35) {
                                    when (k) {
                                        1 -> {
                                            rotate(35.0, 0.4)
                                        }
                                        2 -> {
                                            rotate(-70.0, 0.4)
                                            // If looking at the right mineral
                                            // we consider it to be gold, so the program doesn't
                                            // stop even if it missed the gold
                                            foundGold = true
                                        }
                                    }
                                    k++
                                    elapsedTime.reset()
                                }
                            }
                        }
                    }
                    telemetry.update()
                }
            }
        }
    }
    if (tfod != null) {
        tfod?.shutdown()
    }

    return when (k) {
        1 -> "CENTER"
        2 -> "LEFT"
        3 -> "RIGHT"
        else -> "RIGHT"
    }
}