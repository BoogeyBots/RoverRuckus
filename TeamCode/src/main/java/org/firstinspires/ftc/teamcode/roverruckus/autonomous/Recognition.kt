package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import android.sax.TextElementListener
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.LABEL_GOLD_MINERAL
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.LABEL_SILVER_MINERAL
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.TFOD_MODEL_ASSET
import java.util.Collections.addAll
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.teamcode.roverruckus.utils.elapsedTime

enum class GoldPos {
    Left,
    Middle,
    Right
}

enum class DepotPos {
    Left,
    Right
}

fun Robot.initVuforia() {
    /*
     * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
     */
    val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.packageName)
    val parameters = VuforiaLocalizer.Parameters(cameraMonitorViewId)

    parameters.vuforiaLicenseKey = Robot.VUFORIA_KEY
    parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

    //  Instantiate the Vuforia engine
    vuforia = ClassFactory.getInstance().createVuforia(parameters)

    // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
}

fun Robot.initTfod() {
    val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
    val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)

    tfodParameters.minimumConfidence = 0.75

    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia)
    tfod?.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
}

fun Robot.recognizeGold(): GoldPos {
    var k = 1

    if (opModeIsActive) {
        /** Activate Tensor Flow Object Detection.  */
        if (tfod != null) {
            tfod?.activate()
        }

        var foundGold = false
        var stopwatch = ElapsedTime()
        val TIME_PER_RECOGNITION = 1.0

        while (opModeIsActive && !foundGold) {
            if (tfod != null) {
                val updatedRecognitions = tfod?.updatedRecognitions
                if (updatedRecognitions != null) {
                    telemetry.addData("# Objects Detected", updatedRecognitions.size)
                    if (updatedRecognitions.size == 1) {
                        if (updatedRecognitions[0].label == LABEL_GOLD_MINERAL) {
                            foundGold = true
                            if (stopwatch.seconds() >= TIME_PER_RECOGNITION) {
                                k++
                                stopwatch.reset()
                            }
                        }
                    }
                }
            }

            if (!foundGold && stopwatch.seconds() >= TIME_PER_RECOGNITION) {
                when (k) {
                    1 -> {
                        // strafe left
                        moveByDistance(40.0)
                        k++
                    }
                    2 -> {
                        // strafe right * 2
                        moveByDistance(-80.0)
                        k++
                    }
                }
                stopwatch.reset()
            }

            telemetry.update()
        }
    }

    return when (k) {
        1 -> GoldPos.Middle
        2 -> GoldPos.Right
        else -> GoldPos.Left
    }
}

fun Robot.recognizeVuMark(): DepotPos {
    val targetsRoverRuckus  = this.vuforia!!.loadTrackablesFromAsset("RoverRuckus")

    val blueRover = targetsRoverRuckus[0]
    blueRover.name = "Blue-Rover"
    val redFootprint = targetsRoverRuckus[1]
    redFootprint.name = "Red-Footprint"
    val frontCraters = targetsRoverRuckus[2]
    frontCraters.name = "Front-Craters"
    val backSpace = targetsRoverRuckus[3]
    backSpace.name = "Back-Space"

    val allTrackables = ArrayList<VuforiaTrackable>()
    allTrackables.addAll(targetsRoverRuckus )

    targetsRoverRuckus.activate()

    var foundTarget: String = "NONE"
    while (foundTarget == "NONE" && opModeIsActive) {
        for (trackable in allTrackables) {
            if ((trackable.listener as VuforiaTrackableDefaultListener).isVisible) {
                telemetry.addData("Visible Target", trackable.name)
                foundTarget = trackable.name
                break
            }
        }

        telemetry.addData("working", "working")
        telemetry.update()
    }

    return when (foundTarget) {
        "Back-Space", "Front-Craters" -> DepotPos.Right
        else -> DepotPos.Left
    }
}