package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

@Autonomous(name = "RR - Auto Depot", group = "Rover Ruckus")
class AutoDepot : LinearOpMode() {
    /**
     * [.vuforia] is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private var vuforia: VuforiaLocalizer? = null

    /**
     * [.tfod] is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private var tfod: TFObjectDetector? = null

    private val hardware = RoverRuckusHardware()

    private val SCREEN_WIDTH: Double = 1280.0

    private val MID_WIDTH: Double = SCREEN_WIDTH / 2

    private val rotationTime = 0.5

    private val elapsedTime = ElapsedTime()

    override fun runOpMode() {
        hardware.init(hardwareMap)

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia()

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod()
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD")
        }

        /** Wait for the game to begin  */
        telemetry.addData(">", "Press Play to start tracking")
        telemetry.update()
        waitForStart()

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection.  */
            if (tfod != null) {
                tfod!!.activate()
            }

            var goldPos = -1

            if (tfod != null) {
                while (goldPos == -1) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    val updatedRecognitions = tfod!!.updatedRecognitions
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size)
                        if (updatedRecognitions.size == 3) {
                            var goldMineralX = -1
                            var silverMineral1X = -1
                            var silverMineral2X = -1
                            for (recognition in updatedRecognitions) {
                                when {
                                    recognition.label == LABEL_GOLD_MINERAL -> goldMineralX = recognition.left.toInt()
                                    silverMineral1X == -1 -> silverMineral1X = recognition.left.toInt()
                                    else -> silverMineral2X = recognition.left.toInt()
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                goldPos = if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Left")
                                    0
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Right")
                                    2
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center")
                                    1
                                }
                            }
                        }
                    }
                    telemetry.addData("L_MOTOR Power", hardware.leftMotorPower)
                    telemetry.addData("R_MOTOR_Power", hardware.rightMotorPower)
                    telemetry.update()
                }

                telemetry.addData("L_MOTOR Power", hardware.leftMotorPower)
                telemetry.addData("R_MOTOR_Power", hardware.rightMotorPower)
                telemetry.update()

                when (goldPos) {
                    0 -> { // LEFT
                        hardware.rotateLeft(0.3)
                        hardware.moveForward(time = 2.7)
                    }
                    1 -> { // CENTER
                        hardware.moveForward(time = 3.25)
                    }
                    2 -> { // RIGHT
                        hardware.rotateRight(0.3)
                        hardware.moveForward(time = 2.9)
                    }
                }
            }

            while (opModeIsActive()) { telemetry.update() }
        }

        if (tfod != null) { tfod!!.shutdown() }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private fun initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        val parameters = VuforiaLocalizer.Parameters()

        parameters.vuforiaLicenseKey = VUFORIA_KEY
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters)

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private fun initTfod() {
        val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia)
        tfod!!.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
    }

    companion object {
        private val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
        private val LABEL_GOLD_MINERAL = "Gold Mineral"
        private val LABEL_SILVER_MINERAL = "Silver Mineral"

        /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
        private val VUFORIA_KEY = "AWo7bzb/////AAABmcbdWZ79Y049lfMcsRS8waNYev8AbC1EwUWqhJnr1poItrv7+etQ1bwW4BiQpg151evO66Pzt3L2LvfbBgzn4aQ3QzVBXYQBjqMScjg/gQEj0g3ldi/0ENHSKwnT48YDxtQQb5/twpwjew9wlaSkZuZ8KtZGwOZHh7vhV0xQmjh1akuPF0zmKvCn5HPnd/O9YxXR5Ef7eyQ+r15XMT7Vd7kG/PUbpCvkexwsRZ4BKGv+oV1ZWOqrYrP5WKbpzHmEOl8RggfJKD707G2Q61vTUW+MEksQwrydbwTCqzTxDUTWdOlgzG9JfGjS+jUdQ3CAN+EETNZDOQs8fIxn3Q+Bdmi823AJLEU3GDhptc7KHcjo"
    }
}
