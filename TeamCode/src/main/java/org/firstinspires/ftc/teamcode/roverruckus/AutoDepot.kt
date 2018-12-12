package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.BNO055IMUImpl
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gyroscope
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.robotcore.internal.android.dex.util.Unsigned
import java.util.*
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference



@Autonomous(name = "Dezgatare", group = "Rover Ruckus")
class AutoDepot : LinearOpMode() {
    val hardware = RoverRuckusHardware()
    val elapsedTime = ElapsedTime()

    lateinit var imu: BNO055IMU

    private lateinit var angles: Orientation
    private lateinit var gravity: Acceleration

    var lastAngles: Orientation = Orientation()
    var globalAngle: Double = 0.0

    override fun runOpMode() {
        hardware.init(hardwareMap)

        hardware.lockServoPos = 0.5
        hardware.hookServoPos = 0.5
        hardware.leftIntakeServoPos = 0.0
        hardware.rightIntakeServoPos = 1.00
        hardware.scoopServoPos = 1.0
        telemetry.addData("Servo intake", "L: ${hardware.leftIntakeServoPos}, R: ${hardware.rightIntakeServoPos}")

        val imuParams = BNO055IMU.Parameters()
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES
        imuParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        imuParams.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        imuParams.loggingEnabled = true
        imuParams.loggingTag = "IMU"
        imuParams.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        imu.initialize(imuParams)

        waitForStart()

        imu.startAccelerationIntegration(Position(), Velocity(), 1000)

        composeTelemetry()

        elapsedTime.reset()

        // === RIDIC IN SUS SI SCOT LOCK
        while (elapsedTime.seconds() < 0.28 && opModeIsActive()) {
            hardware.leftArmPower = 0.6
            hardware.rightArmPower = 0.6

            hardware.lockServoPos = Range.clip(hardware.lockServoPos - 0.05, 0.0, 0.5)
        }

        elapsedTime.reset()

        // === LAS USOR IN JOS
        while (elapsedTime.seconds() < 2.8 && opModeIsActive()) {
            hardware.leftArmPower = 0.08
            hardware.rightArmPower = 0.08
        }

        elapsedTime.reset()

        // === MA IMPING IN LANDER
        while (elapsedTime.seconds() < 0.4) {
            hardware.leftArmPower = -0.3
            hardware.rightArmPower = -0.3
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0

        elapsedTime.reset()

        while (elapsedTime.seconds() < 0.5 && opModeIsActive()) {
            // WAIT 0.5 sec
        }

        // === SCOT HOOK
        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
            hardware.hookServoPos = Range.clip(hardware.hookServoPos - 0.05, 0.0, 0.5)
        }

        elapsedTime.reset()

        // === COBOR BRAT
        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
            hardware.leftArmPower = 0.2
            hardware.rightArmPower = 0.2
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0

        // ROTATE 180 DEGREES
        rotate(-165.0, 0.34)

        elapsedTime.reset()
        while (elapsedTime.seconds() < 2.0 && opModeIsActive()) {
            telemetry.update()
        }

        var goldPos: String = "NOT FOUND"

        // TODO detect cube

        when (goldPos) {
            "LEFT" -> {

            }
            "CENTER" -> {

            }
            "RIGHT" -> {

            }
        }
    }

    fun rotateOld() {
        val rotation = imu
                .getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                .firstAngle
                .toDouble()

        do {
            hardware.leftMotorPower = 0.35
            hardware.rightMotorPower = -0.35

            val rotation = imu
                    .getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                    .firstAngle
                    .toDouble()

            telemetry.update()
        } while ((rotation > -170.0 || rotation < 170.0) && opModeIsActive())

        hardware.leftMotorPower = 0.0
        hardware.rightMotorPower = 0.0
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

        hardware.leftMotorPower = 0.0
        hardware.rightMotorPower = 0.0

        // wait for rotation to stop.
        sleep(1000)

        // reset angle tracking on new heading.
        resetAngle()
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
}