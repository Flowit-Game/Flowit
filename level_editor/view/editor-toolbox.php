<?php
    
function posArg() {
    echo "c=" . $_GET["c"] . "&r=" . $_GET["r"];
}

?>

<?php function direction($d) { ?>
    <td>
        <a class="field action" href="./?action=save_<?= $d ?>&<?php posArg(); ?>">
            <img src="./drawable/level_box_color_none.png" />
            <img src="./drawable/level_box_type_limit_<?= $d ?>.png" />
        </a>
    </td>
<?php } ?>

<div class="col card p-0 m-2">
    <div class="card-header">
        Edit selected position
    </div>
    <div class="editor card-body">
        <h6>Select type</h6>
        <table class="m-2">
            <tr>
                <td>
                    <a class="field action0" href="./?action=save_type&<?php posArg(); ?>&type=0">
                        <img src="./drawable/level_box_hole.png" />
                    </a>
                </td>
                <td>
                    <a class="field action0" href="./?action=save_type&<?php posArg(); ?>&type=1">
                        <img src="./drawable/level_box_color_none.png"/>
                        <img src="./drawable/level_box_type_flow.png"/>
                    </a>
                </td>
                <?php if (BOMBS) {?>
                    <td>
                        <a class="field action0" href="./?action=save_type&<?php posArg(); ?>&type=4">
                            <img src="./drawable/level_box_color_none.png" />
                            <img src="./drawable/level_box_type_bomb.png" />
                        </a>
                    </td>
                <?php } ?>
                <td>
                    <a class="field action0" href="./?action=save_type&<?php posArg(); ?>&type=2">
                        <img src="./drawable/level_box_color_none.png" />
                        <img src="./drawable/level_box_type_limit_up.png" />
                    </a>
                </td>
                <td>
                    <a class="field action0" href="./?action=save_type&<?php posArg(); ?>&type=3">
                        <img src="./drawable/level_nothing.png" />
                    </a>
                </td>
            </tr>
        </table>

        <?php if (!isset($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"])) { ?>
            <div class="alert alert-secondary">
                No position selected. Click on a box on the game board!
            </div>
        <?php } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_EMPTY) { ?>
            <h6>Select target color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field" href="./?action=save_color&<?php posArg(); ?>&color=<?= $a ?>">
                                <img src="./drawable/level_box_dest_color_<?= $b ?>.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
            <h6>Select prefilled color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { ?>
                        <td>
                            <a class="field action" href="./?action=save_preset&<?php posArg(); ?>&color=<?= $a ?>">
                                <img src="./drawable/level_box_hole.png" />
                                <?php if ($b != "") { ?>
                                    <img src="./drawable/level_box_stone_<?= $b ?>.png" />
                                <?php } ?>
                            </a>
                        </td>
                    <?php } ?>
                </tr>
            </table>
        <?php } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_FLOW) { ?>
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field action" href="./?action=save_color&<?php posArg(); ?>&color=<?= $a ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_flow.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
        <?php } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_BOMB) { ?>
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field action" href="./?action=save_color&<?php posArg(); ?>&color=<?= $a ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_bomb.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
        <?php } else if ($_SESSION["level_data"][$_GET["r"]][$_GET["c"]]["type"] == TYPE_LIMIT) { ?>
            <h6>Select color</h6>
            <table class="m-2">
                <tr>
                    <?php foreach (COLOR_MAPPING as $a => $b) { if ($b != "") { ?>
                        <td>
                            <a class="field action" href="./?action=save_color&<?php posArg(); ?>&color=<?= $a ?>">
                                <img src="./drawable/level_box_color_<?= $b ?>.png" />
                                <img src="./drawable/level_box_type_limit_up.png" />
                            </a>
                        </td>
                    <?php } } ?>
                </tr>
            </table>
            <h6>Select direction</h6>
            <table class="m-2">
                <tr>
                    <?php 
                        direction("up");
                        direction("down");
                        direction("left");
                        direction("right");
                    ?>
                </tr><tr>
                    <?php 
                        if (ROTATING) {
                            direction("rotate_up");
                            direction("rotate_down");
                            direction("rotate_left");
                            direction("rotate_right");
                        }
                    ?>
                </tr>
            </table>
        <?php } ?>
    </div>
</div>
