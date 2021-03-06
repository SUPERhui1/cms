<#macro showTree Tree>

    <#if Tree.children??>
                <li class="nav-item dropdown">
                    <a href="/view/category/${Tree.id}#" class="dropdown-toggle nav-link" data-toggle="dropdown"
                       role="button"
                       aria-haspopup="true"
                       aria-expanded="false">
                        ${Tree.name}
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu" style="border-radius:.6rem;opacity: 0.8;min-width:5rem;background-color:rgba(0,0,0,0.2);border:0;font-size: .9rem;">
                    <#list Tree.children as t>
                    <@showTree Tree=t/>
                    </#list>
                    </ul>
                </li>
    <#else>
            <li class="nav-item">
                <a style="font-size: 12px;font-weight: 600;color: #ffffff" class="nav-link" href="/view/category/${Tree.id}">${Tree.name}</a>
            </li>
    </#if>


</#macro>

<#macro header site TreeList>
<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
    <div class="container">
        <a class="navbar-brand" href="/${site.siteUrl}">${site.siteName}</a>
        <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse"
                data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false"
                aria-label="Toggle navigation">
            Menu
            <i class="fa fa-bars"></i>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">

            <ul class="navbar-nav ml-auto">
                <#list TreeList as tree>
                    <@showTree Tree=tree></@showTree>
                </#list>
            </ul>

        </div>
    </div>
</nav>
<script>
    window.onload = function () {
        $(".dropdown").hover(function () {
                    $(this).children("a").dropdown("toggle")
                },
                function () {
                    $(this).children("a").dropdown("toggle")
                });
    }
</script>
</#macro>

