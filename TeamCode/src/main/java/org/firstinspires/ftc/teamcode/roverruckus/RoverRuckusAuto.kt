package org.firstinspires.ftc.teamcode.roverruckus

import com.disnodeteam.dogecv.CameraViewDisplay
import com.disnodeteam.dogecv.DogeCV
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry

@Autonomous(name = "RR Auto - Crater Front", group = "Rover Ruckus")
class RoverRuckusAuto : OpMode() {
    private val hardware: RoverRuckusHardware = RoverRuckusHardware()
    private val detector: GoldAlignDetector = GoldAlignDetector()

    private val elapsedTime: ElapsedTime = ElapsedTime()
    private var passedCube: Boolean = false
    private var startedCounting: Boolean = false

    private val telemea: HashMap<String, Telemetry.Item> = HashMap()

    override fun init() {
        hardware.init(hardwareMap)

        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()) // Initialize it with the app context and camera
        detector.useDefaults() // Set detector to use default settings

        // Optional tuning
        detector.alignSize = 100.0 // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 0.0 // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4 // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005 //

        detector.ratioScorer.weight = 5.0 //
        detector.ratioScorer.perfectRatio = 1.0 // Ratio adjustment

        detector.enable() // Start the detector!

        telemetry.isAutoClear = false

        telemea["x_pos"] = telemetry.addData("Cube X Position", -200)
        telemea["aligned"] = telemetry.addData("Is the cube aligned?", false)
        telemea["passed_cube"] = telemetry.addData("Passed the cube?", false)
        telemea["move_time"] = telemetry.addData("Move time:", 0)
        telemea["align_offset"] = telemetry.addData("Align offset", 2000000)
    }

    override fun loop() {
        if (!passedCube) {
            if (!detector.isFound || !detector.aligned) {
                rotateLeft()
            } else if (Math.abs(detector.xPosition - detector.downscaleResolution.width / 2.0) < 10.0) {
                if (!startedCounting) {
                    elapsedTime.reset()
                    startedCounting = true
                }
                if (elapsedTime.seconds() >= 3.0) {
                    moveForward()
                    telemea["move_time"]?.setValue(elapsedTime.seconds())
                    passedCube = true
                    resetMotors()
                }
            }
        }

        telemea["aligned"]?.setValue(detector.aligned)
        telemea["x_pos"]?.setValue(detector.xPosition)
        telemea["passed_cube"]?.setValue(passedCube)
        telemea["move_time"]?.setValue(elapsedTime.seconds())
        telemea["align_offset"]?.setValue(Math.abs(detector.xPosition - detector.downscaleResolution.width / 2.0))
        telemetry.update()
    }

    private fun rotateLeft() {
        hardware.leftMotorPower = 0.1
        hardware.rightMotorPower = -0.1
    }

    private fun moveForward() {
        hardware.leftMotorPower = 0.3
        hardware.rightMotorPower = 0.3
    }

    private fun resetMotors() {
        hardware.leftMotorPower = 0.0
        hardware.rightMotorPower = 0.0
    }
}