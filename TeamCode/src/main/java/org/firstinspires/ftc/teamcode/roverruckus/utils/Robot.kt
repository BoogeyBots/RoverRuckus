package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

enum class Motors {
    LF,
    RF,
    LB,
    RB,
    Lift,
    IntakeRotation,
    IntakeExtension
}

enum class Servos {
    Phone
}

class Robot(val opMode: OpMode) {
    lateinit var motors: HashMap<Motors, DcMotor>
    lateinit var servos: HashMap<Servos, Servo>

    val telemetry
        get() = opMode.telemetry

    val hardwareMap
        get() = opMode.hardwareMap

    val opModeIsActive
        get() = (opMode as LinearOpMode).opModeIsActive()

    lateinit var imu: BNO055IMU

    lateinit var angles: Orientation
    lateinit var gravity: Acceleration

    var lastAngles: Orientation = Orientation()
    var globalAngle: Double = 0.0

    var vuforia: VuforiaLocalizer? = null
    var tfod: TFObjectDetector? = null

    fun init() {
        motors = hashMapOf(
                Motors.LF to hardwareMap.get(DcMotor::class.java, "lf"),
                Motors.RF to hardwareMap.get(DcMotor::class.java, "rf"),
                Motors.LB to hardwareMap.get(DcMotor::class.java, "lb"),
                Motors.RB to hardwareMap.get(DcMotor::class.java, "rb"),
                Motors.Lift to hardwareMap.get(DcMotor::class.java, "lift"),
                Motors.IntakeRotation to hardwareMap.get(DcMotor::class.java, "i_r"),
                Motors.IntakeExtension to hardwareMap.get(DcMotor::class.java, "i_e")
        )

        motors[Motors.IntakeRotation]?.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        motors[Motors.Lift]?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motors[Motors.IntakeRotation]?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motors[Motors.IntakeExtension]?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE


        motors[Motors.LF]?.direction = DcMotorSimple.Direction.REVERSE
        motors[Motors.RF]?.direction = DcMotorSimple.Direction.FORWARD
        motors[Motors.LB]?.direction = DcMotorSimple.Direction.REVERSE
        motors[Motors.RB]?.direction = DcMotorSimple.Direction.FORWARD

        servos = hashMapOf(
                Servos.Phone to hardwareMap.get(Servo::class.java, "sph")
        )

        val imuParams = BNO055IMU.Parameters()
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES
        imuParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        imuParams.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        imuParams.loggingEnabled = true
        imuParams.loggingTag = "IMU"
        imuParams.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        imu.initialize(imuParams)
    }

    fun setMotorMode(motor: Motors, mode: DcMotor.RunMode) {
        this.motors[motor]?.mode = mode
    }

    fun setMotorsMode(mode: DcMotor.RunMode, vararg motors: Motors) {
        for (motor in motors) {
            setMotorMode(motor, mode)
        }
    }

    fun setMotorPower(motor: Motors, power: Double) {
        this.motors[motor]?.power = power
    }

    fun setMotorsPower(power: Double, vararg motors: Motors) {
        for (motor in motors) {
            setMotorPower(motor, power)
        }
    }

    fun setMotorTargetPos(motor: Motors, position: Double) {
        this.motors[motor]?.targetPosition = position.toInt()
    }

    fun setMotorsTargetPos(position: Double, vararg motors: Motors) {
        for (motor in motors) {
            setMotorTargetPos(motor, position)
        }
    }

    fun isMotorBusy(motor: Motors): Boolean {
        return motors[motor]?.mode == DcMotor.RunMode.RUN_TO_POSITION && motors[motor]?.isBusy!!
    }

    fun areAllMotorsBusy(vararg motors: Motors): Boolean {
        return motors.all { m -> isMotorBusy(m) }
    }

    fun areMotorsBusy(vararg motors: Motors): Boolean {
        return motors.any { m -> isMotorBusy(m) }
    }

    companion object {
        const val DEFAULT_MOTOR_POWER = 0.5
        const val MOVEMENT_MOTOR_TICK_COUNT = 1440
        const val WHEEL_DIAMETER = 4.0 // inches
        const val RATIO = 3.0
        const val WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER // inches

        val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
        val LABEL_GOLD_MINERAL = "Gold Mineral"
        val LABEL_SILVER_MINERAL = "Silver Mineral"

        val VUFORIA_KEY = "AWo7bzb/////AAABmcbdWZ79Y049lfMcsRS8waNYev8AbC1EwUWqhJnr1poItrv7+etQ1bwW4BiQpg151evO66Pzt3L2LvfbBgzn4aQ3QzVBXYQBjqMScjg/gQEj0g3ldi/0ENHSKwnT48YDxtQQb5/twpwjew9wlaSkZuZ8KtZGwOZHh7vhV0xQmjh1akuPF0zmKvCn5HPnd/O9YxXR5Ef7eyQ+r15XMT7Vd7kG/PUbpCvkexwsRZ4BKGv+oV1ZWOqrYrP5WKbpzHmEOl8RggfJKD707G2Q61vTUW+MEksQwrydbwTCqzTxDUTWdOlgzG9JfGjS+jUdQ3CAN+EETNZDOQs8fIxn3Q+Bdmi823AJLEU3GDhptc7KHcjo"
    }
}
