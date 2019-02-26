package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.LABEL_GOLD_MINERAL
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.LABEL_SILVER_MINERAL
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.TFOD_MODEL_ASSET
import java.util.Collections.addAll



enum class GoldPos {
    Left,
    Middle,
    Right
}

fun Robot.initVuforia() {
    /*
     * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
     */
    vuforia = VuforiaRoverRuckus()
    val parameters = VuforiaRoverRuckus.createParameters()

    parameters.vuforiaLicenseKey = Robot.VUFORIA_KEY
    parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

    //  Instantiate the Vuforia engine
    vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters)

    // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
}

fun Robot.initTfod() {
    val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
    val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)

    tfodParameters.minimumConfidence = 0.75

    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforiaLocalizer)
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
        val MAX_RECOGNITION_TIME = 0.5
        val TIME_PER_RECOGNITION = 0.25

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

            if (stopwatch.seconds() >= TIME_PER_RECOGNITION) {
                when (k) {
                    1 -> {
                        // strafe left
                        k++
                    }
                    2 -> {
                        // strafe right * 2
                        k++
                    }
                }
            }

            telemetry.update()
        }
    }

    return when (k) {
        1 -> GoldPos.Middle
        2 -> GoldPos.Left
        else -> GoldPos.Right
    }
}

fun Robot.recognizeVuMark() {
    var lastLocation = OpenGLMatrix()
    val trackables: VuforiaTrackables = vuforiaLocalizer!!.loadTrackablesFromAsset("RoverRuckus")

    val blueRover = trackables[0]
    blueRover.name = "Blue-Rover"
    val redFootprint = trackables[1]
    redFootprint.name = "Red-Footprint"
    val frontCraters = trackables[2]
    frontCraters.name = "Front-Craters"
    val backSpace = trackables[3]
    backSpace.name = "Back-Space"

    val allTrackables = ArrayList<VuforiaTrackable>()
    allTrackables.addAll(trackables)
}