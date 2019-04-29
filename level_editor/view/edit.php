<div class="container p-3">
    <div class="row">
        <div class="col card p-0 m-2">
            <div class="card-header">
                Game board
            </div>
            <div class="card-body">
                <?php
                    drawBoard($_SESSION["level_data"], "edit", $_GET["c"], $_GET["r"],
                            @$_GET["action"] == "edit");
                ?>
            </div>
        </div>
        <?php
            if (@$_GET["action"] == "edit") {
                include("view/editor-toolbox.php");
            } 
        ?>
    </div>
</div>