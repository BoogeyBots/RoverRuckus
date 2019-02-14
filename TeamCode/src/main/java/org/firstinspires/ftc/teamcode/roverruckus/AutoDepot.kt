package org.firstinspires.ftc.teamcode.roverruckus

import android.media.MediaPlayer
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import com.vuforia.CameraDevice
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.*
import java.util.*
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector


@Autonomous(name = "Depot", group = "Rover Ruckus")
class AutoDepot : LinearOpMode() {
    val hardware = RoverRuckusHardware()
    val elapsedTime = ElapsedTime()

    lateinit var imu: BNO055IMU

    private lateinit var angles: Orientation
    private lateinit var gravity: Acceleration

    var lastAngles: Orientation = Orientation()
    var globalAngle: Double = 0.0

    private val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
    private val LABEL_GOLD_MINERAL = "Gold Mineral"
    private val LABEL_SILVER_MINERAL = "Silver Mineral"

    private val VUFORIA_KEY = "AWo7bzb/////AAABmcbdWZ79Y049lfMcsRS8waNYev8AbC1EwUWqhJnr1poItrv7+etQ1bwW4BiQpg151evO66Pzt3L2LvfbBgzn4aQ3QzVBXYQBjqMScjg/gQEj0g3ldi/0ENHSKwnT48YDxtQQb5/twpwjew9wlaSkZuZ8KtZGwOZHh7vhV0xQmjh1akuPF0zmKvCn5HPnd/O9YxXR5Ef7eyQ+r15XMT7Vd7kG/PUbpCvkexwsRZ4BKGv+oV1ZWOqrYrP5WKbpzHmEOl8RggfJKD707G2Q61vTUW+MEksQwrydbwTCqzTxDUTWdOlgzG9JfGjS+jUdQ3CAN+EETNZDOQs8fIxn3Q+Bdmi823AJLEU3GDhptc7KHcjo"
    private var vuforia: VuforiaLocalizer? = null
    private var tfod: TFObjectDetector? = null


    private lateinit var mediaPlayer: MediaPlayer

    @Throws(InterruptedException::class)
    override fun runOpMode() {
        hardware.init(hardwareMap)

        hardware.lockServoPos = 0.5
        hardware.hookServoPos = 0.5

        val imuParams = BNO055IMU.Parameters()
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES
        imuParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        imuParams.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        imuParams.loggingEnabled = true
        imuParams.loggingTag = "IMU"
        imuParams.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        imu.initialize(imuParams)

        telemetry.addData("INIT", "over!")
        telemetry.update()

//      waitForStart()
        while (!opModeIsActive() && !isStopRequested) {
            telemetry.addData("Status", "Waiting in Init")
            telemetry.update()
        }

        imu.startAccelerationIntegration(Position(), Velocity(), 1000)

        composeTelemetry()

        liftLock()

        dropDown()

        pushLander()

        waitForSeconds(0.5)

        goTowardsLanderToLiftHook()

        waitForSeconds(0.5)

        detachHook()

        elapsedTime.reset()
        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
            hardware.leftArmPower = 0.2
            hardware.rightArmPower = 0.2
        }
        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0


        // ROTATE 180 DEGREES
        rotate(-165.0, 0.34)

        waitForSeconds(0.25)

        liftArm()

        initVuforia()

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod()
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD")
        }
        CameraDevice.getInstance().setFlashTorchMode(true);


        goForward(time=0.2, power=0.3)

        var goldPos = 1
        var k = 1

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection.  */
            if (tfod != null) {
                tfod?.activate()
            }

            elapsedTime.reset()

            var foundGold = false
            while (opModeIsActive() && !foundGold) {
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
                                                rotate(30.0, 0.34)
                                            }
                                            2 -> {
                                                rotate(-60.0, power = 0.34)
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

        CameraDevice.getInstance().setFlashTorchMode(false);

        when (k) {
            1 -> {
                goldPos = 2
            }
            2 -> {
                goldPos = 1
            }
            3 -> {
                goldPos = 3
            }
        }

        if (tfod != null) {
            tfod?.shutdown()
        }

        /*
        *  goldPos legend
        *  1 -> left
        *  2 -> center
        *  3 -> right
        * */

        when (goldPos) {
            1 -> {
                goForwardOnAngle(time = 1.0, power=0.35, angle=-135.0)
                goForwardOnAngle(time = 0.5, power=0.45, angle=-145.0)
            }
            2 -> {
                goForwardOnAngle(time = 1.3, power = 0.45, angle = 180.0)
                rotate(47.0, power = 0.34)
                goForwardOnAngle(time = 0.24, power = 0.65, angle = 135.0)
            }
            3 -> {
                goForwardOnAngle(time = 0.9, power = 0.45, angle = 145.0)
            }
        }

        resetMovementMotors()

        waitForSeconds(0.5)

        elapsedTime.reset()
        while (elapsedTime.seconds() < 0.5 && opModeIsActive()) {

        }

        // ===================================
        // ====== GOING INTO THE CRATER ======
        // ===================================

        when (goldPos) {
            1-> {
                rotate(degrees = 85.0, power = 0.34)
                goForwardOnAngle(time = 1.0, power = -0.45, angle = -42.0)
                dropTeamMarker()
                goForwardOnAngle(time = 0.6, power = 0.55, angle = -40.0)
                liftIntake()
                goForwardOnAngle(time = 1.1, power = 0.55, angle = -43.0)
                letDownArm()
            }
            2 -> {
                rotate(degrees = 70.0, power = 0.34)
                dropTeamMarker()
                letDownArm()
                goForwardOnAngle(time = 0.6, power = 0.6, angle = -45.0)
                liftIntake()
                goForwardOnAngle(time = 0.7, power = 0.55, angle = -36.0)
                goForwardOnAngle(time = 0.65, power = 0.55, angle = -40.0)
            }
            3 -> {
                rotate(degrees = -80.0, power = 0.34)
                goForwardOnAngle(time = 1.27, power = -0.55, angle = 45.0)
                rotate(-80.0, power = 0.34)
                dropTeamMarker()
                letDownArm()
                goForwardOnAngle(time = 0.6, power = 0.50, angle = -40.0)
                liftIntake()
                goForwardOnAngle(time = 0.75, power = 0.50, angle = -37.0)
                goForwardOnAngle(time = 0.65, power = 0.50, angle = -42.0)
            }
        }

        /*
        * ==============
        * ==== STOP ====
        * ==============
        * */
    }

    private fun liftArm() {
        elapsedTime.reset()
        while (elapsedTime.seconds() < 0.4 && opModeIsActive()) {
            hardware.leftArmPower = 0.2
            hardware.rightArmPower = 0.2
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0
    }

    private fun liftIntake() {
        elapsedTime.reset()
        while (elapsedTime.seconds() < 0.4 && opModeIsActive()) {
        }
    }

    private fun dropTeamMarker() {
        elapsedTime.reset()
        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
        }
    }

    private fun goForward(time: Double, power: Double = 0.3) {
        elapsedTime.reset()
        while (elapsedTime.seconds() < time && opModeIsActive()) {
            hardware.leftMotorPower = power
            hardware.rightMotorPower = power
        }

        resetMovementMotors()
    }

    private fun letDownArm() {
        // === COBOR BRAT
        elapsedTime.reset()
        while (elapsedTime.seconds() < 0.43 && opModeIsActive()) {
            hardware.leftArmPower = -0.2
            hardware.rightArmPower = -0.2
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0
    }

    private fun detachHook() {
        elapsedTime.reset()
        // === SCOT HOOK
        while (elapsedTime.seconds() < 1.5 && opModeIsActive()) {
            hardware.hookServoPos = Range.clip(hardware.hookServoPos + 0.05, 0.5, 1.0)
        }
    }

    private fun moveLock() {
        elapsedTime.reset()

        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
            hardware.lockServoPos = Range.clip(hardware.lockServoPos + 0.05, 0.0, 0.5)
        }
    }

    private fun goTowardsLanderToLiftHook() {
        elapsedTime.reset()

        // MERG SPRE LANDER CA SA FIE HOOKU MAI SUS
        while (elapsedTime.seconds() < 0.1 && opModeIsActive()) {
            hardware.leftMotorPower = 0.4
            hardware.rightMotorPower = 0.4
        }

        resetMovementMotors()
    }

    private fun waitForSeconds(seconds: Double) {
        elapsedTime.reset()

        while (elapsedTime.seconds() < seconds && opModeIsActive()) {
            // WAIT 0.75 secs
        }
    }

    private fun pushLander() {
        elapsedTime.reset()

        // === MA IMPING IN LANDER
        while (elapsedTime.seconds() < 0.7 && opModeIsActive()) {
            hardware.leftArmPower = -0.3
            hardware.rightArmPower = -0.3
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0
    }

    private fun dropDown() {
        elapsedTime.reset()

        // === LAS USOR IN JOS
        while (elapsedTime.seconds() < 2.8 && opModeIsActive()) {
            hardware.leftArmPower = 0.08
            hardware.rightArmPower = 0.08
        }
    }

    private fun liftLock() {
        elapsedTime.reset()

        // === RIDIC IN SUS SI SCOT LOCK
        while (elapsedTime.seconds() < 0.28 && opModeIsActive()) {
            hardware.leftArmPower = 0.6
            hardware.rightArmPower = 0.6

            hardware.lockServoPos = Range.clip(hardware.lockServoPos - 0.05, 0.0, 0.5)
        }
    }

    fun goForwardOnAngle(time: Double, power: Double, angle: Double) {
        elapsedTime.reset()
        val leftLimiter: (Double) -> Boolean
        val rightLimiter: (Double) -> Boolean
        val higherPower = power + 0.25
        val lowerPower = power - 0.25
        if (angle == 180.0) {
            leftLimiter = { rotation -> rotation < 0 && rotation > -177.5 }
            rightLimiter = { rotation -> rotation > 0 && rotation < 177.5 }
        } else {
            leftLimiter = { rotation -> rotation > angle + 2.5 }
            rightLimiter = { rotation -> rotation < angle - 2.5 }
        }
        while (elapsedTime.seconds() < time && opModeIsActive()) {
            val rotation = imu
                    .getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                    .firstAngle
                    .toDouble()
            when {
                leftLimiter(rotation) -> {
                    hardware.leftMotorPower = higherPower
                    hardware.rightMotorPower = lowerPower
                }
                rightLimiter(rotation) -> {
                    hardware.leftMotorPower = lowerPower
                    hardware.rightMotorPower = higherPower
                }
                else -> {
                    hardware.leftMotorPower = power
                    hardware.rightMotorPower = power
                }
            }
            if (power < 0.0) { telemetry.addData("Move", "Backwards") }
            telemetry.addData("Movement", "LM: ${hardware.leftMotorPower}; RM: ${hardware.rightMotorPower}")
            telemetry.update()
        }

        resetMovementMotors()
    }

    private fun resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

        globalAngle = 0.0
    }

    fun getAngle(): Double {
        val angles: Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

        var deltaAngle = angles.firstAngle - lastAngles.firstAngle

        if (deltaAngle < -180) {
            deltaAngle += 360
        } else if (deltaAngle > 180) {
            deltaAngle -= 360
        }

        globalAngle += deltaAngle

        lastAngles = angles

        return globalAngle
    }

    fun rotate(degrees: Double, power: Double) {
        var leftPower: Double
        var rightPower: Double

        when {
            degrees < 0 -> {
                leftPower = power
                rightPower = -power
            }
            degrees > 0 -> {
                leftPower = -power
                rightPower = power
            }
            else -> return
        }

        hardware.leftMotorPower = leftPower
        hardware.rightMotorPower = rightPower

        if (degrees - getAngle() < 0) {
            while (opModeIsActive() && getAngle() == 0.0) { telemetry.update() }

            while (opModeIsActive() && getAngle() > degrees) { telemetry.update() }
        } else {
            while (opModeIsActive() && getAngle() < degrees) { telemetry.update() }
        }

        resetMovementMotors()

        // wait for rotation to stop.
        sleep(1000)

        // reset angle tracking on new heading.
        resetAngle()
    }

    private fun resetMovementMotors() {
        hardware.leftMotorPower = 0.0
        hardware.rightMotorPower = 0.0
    }


    fun moveForwardOnAngle(angle: Double, time: Double) {
        while (elapsedTime.seconds() < time && opModeIsActive()) {
            val rotation = imu
                    .getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                    .firstAngle
                    .toDouble()
            when {
                rotation > angle + 5.0 -> {
                    hardware.leftMotorPower = 0.55
                    hardware.rightMotorPower = 0.35
                }
                rotation < angle - 5.0 -> {
                    hardware.leftMotorPower = 0.35
                    hardware.rightMotorPower = 0.55
                }
                else -> {
                    hardware.leftMotorPower = 0.4
                    hardware.rightMotorPower = 0.4
                }
            }
            telemetry.update()
        }
    }

    internal fun composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
            gravity = imu.gravity
        }

        telemetry.addLine()
                .addData("status") { imu.systemStatus.toShortString() }
                .addData("calib") { imu.calibrationStatus.toString() }

        telemetry.addLine()
                .addData("heading") { formatAngle(angles.angleUnit, angles.firstAngle.toDouble()) }
                .addData("Global angle") { globalAngle }

        telemetry.addLine()
                .addData("grvty") { gravity.toString() }
                .addData("mag") {
                    String.format(Locale.getDefault(), "%.3f",
                            Math.sqrt(gravity.xAccel * gravity.xAccel
                                    + gravity.yAccel * gravity.yAccel
                                    + gravity.zAccel * gravity.zAccel))
                }
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    internal fun formatAngle(angleUnit: AngleUnit, angle: Double): String {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle))
    }

    internal fun formatDegrees(degrees: Double): String {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees))
    }

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
        tfod?.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
    }
}